package com.seven.nicespinner;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seven.nicespinner.R;
import java.util.ArrayList;

/**
 * Created by seven on 2016/4/28.
 */
public class NiceSpinner extends RelativeLayout implements View.OnClickListener {

    private static final int DEFAULT_MORE_COUNT = 5;
    private Context mContext;
    private DisplayMetrics dm;
    private int screenWidth;
    private int screenHeight;
    //PopupWindow对象声明
    private PopupWindow mPopupWindow;
    private ArrayList<String> mList;
    private View mPopView;
    //当前选中的列表项位置
    private int clickPsition = 0;
    private TextView spinnerText;
    private ImageView arrowImg;
    private Animation rotateUp;
    private Animation rotateDown;
    private NiceSpinnerCallBack mCallBack;
    private ListViewAdapter mAdapter;
    private int moreCount;//加载更多数据
    private int mRowNum;//最多显示多少条数据
    private int mDefaultCount;//最多显示多少条数据
    private int spinnerListWidth;
    private int spinnerListHeight;

    public NiceSpinner(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public NiceSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }


    public void init() {

        dm = mContext.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        this.setOnClickListener(this);

        spinnerText = new TextView(mContext) {
            @Override
            public boolean isFocused() {
                return true;
            }
        };
        //spinnerText.setMaxLines(1);
        //跑马灯效果
        spinnerText.setSingleLine(true);
        spinnerText.setFocusable(true);
        spinnerText.setFocusableInTouchMode(true);
        spinnerText.setEllipsize(TextUtils.TruncateAt.MARQUEE);//跑马灯样式
        spinnerText.setMarqueeRepeatLimit(-1);//无限循环

        spinnerText.setGravity(Gravity.CENTER);
        spinnerText.setId(R.id.spinner_text);
        spinnerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);//unit->1代表dp
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(screenWidth / 4
                , LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        arrowImg = new ImageView(mContext);
        arrowImg.setImageResource(R.drawable.icon_spinner_arrow);
        rotateUp = AnimationUtils.loadAnimation(mContext, R.anim.spinner_arrow_animation_up);//创建动画
        rotateUp.setInterpolator(new LinearInterpolator());//设置为线性旋转
        rotateDown = AnimationUtils.loadAnimation(mContext, R.anim.spinner_arrow_animation_down);//创建动画
        rotateDown.setInterpolator(new LinearInterpolator());//设置为线性旋转

        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageParams.addRule(RelativeLayout.CENTER_VERTICAL);
        imageParams.addRule(RelativeLayout.RIGHT_OF, spinnerText.getId());

        this.addView(spinnerText, textParams);
        this.addView(arrowImg, imageParams);


    }

    public void setText(String text) {
        spinnerText.setText(text);
    }

    public String getText() {
        return spinnerText.getText().toString();
    }

    public void setDataList(ArrayList<String> list) {
        this.mList = list;
        if (spinnerText.getText().toString() == null ||
                spinnerText.getText().toString().equals(""))
            spinnerText.setText(this.mList.get(0));
    }


    @Override
    public void onClick(View v) {

        rotateUp.setFillAfter(true);
        arrowImg.startAnimation(rotateUp);

        spinnerListWidth = spinnerListWidth == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : spinnerListWidth;
        spinnerListHeight = spinnerListHeight == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : spinnerListHeight;

        //通过布局注入器，注入布局给View对象
        mPopView = LayoutInflater.from(mContext).inflate(R.layout.layout_nice_spinner, null);
        //通过view 和宽·高，构造PopopWindow
        mPopupWindow = new PopupWindow(mPopView, spinnerListWidth, spinnerListHeight, true);
        //此处为popwindow 设置背景，同事做到点击外部区域，popwindow消失
        //mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
        //设置焦点为可点击
        mPopupWindow.setFocusable(true);//可以试试设为false的结果
        //将window视图显示在NiceSpinner下面
        mPopupWindow.showAsDropDown(this);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                rotateDown.setFillAfter(true);
                // !rotate.getFillAfter() 每次都取相反值，使得可以不恢复原位的旋转
                arrowImg.startAnimation(rotateDown);
            }
        });
        ListView lv = (ListView) mPopView.findViewById(R.id.list_nice_spinner);
        mAdapter = new ListViewAdapter(mContext, mList);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (clickPsition != position) {
                    clickPsition = position;
                }
                mPopupWindow.dismiss();
                moreCount = mDefaultCount;
                if (mCallBack != null) {
                    mCallBack.setText(mList.get(position), NiceSpinner.this);
                } else {
                    spinnerText.setText(mList.get(position));
                }
            }
        });

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 判断滚动到底部
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    if (mCallBack != null && moreCount < mRowNum) {
                        moreCount += DEFAULT_MORE_COUNT;
                        mCallBack.loadData(moreCount, NiceSpinner.this);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        lv.setSelection(clickPsition);

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        arrowImg.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    public int getCurrentPosition() {
        return clickPsition;
    }

    public int getSpinnerListWidth() {
        return spinnerListWidth;
    }

    public void setSpinnerListWidth(int spinnerListWidth) {
        this.spinnerListWidth = spinnerListWidth;
    }

    public int getSpinnerListHeight() {
        return spinnerListHeight;
    }

    public void setSpinnerListHeight(int spinnerListHeight) {
        this.spinnerListHeight = spinnerListHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeigh() {
        return screenHeight;
    }

    public void setDataCount(int rowNum, int defaultCount) {
        mRowNum = rowNum;
        mDefaultCount = defaultCount;
        moreCount = mDefaultCount;
    }

    public void refresh(ArrayList<String> list) {
        if (mAdapter != null) {
            this.mList = list;
            mAdapter.setDataList(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ListViewAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        private ArrayList<String> list;


        public ListViewAdapter(Context context, ArrayList<String> list) {
            super();
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        public void setDataList(ArrayList<String> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_nice_spinner, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.spinner_text);
            tv.setText(list.get(position));
            ImageView imgView = (ImageView) convertView.findViewById(R.id.spinner_select);
            if (clickPsition == position) {
                imgView.setVisibility(View.VISIBLE);
            } else {
                imgView.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

    }


    /**
     * listView回调接口
     */
    public interface NiceSpinnerCallBack {

        void loadData(int moreCount, View view);

        void setText(String text, View view);

    }

    public void addCallBack(NiceSpinnerCallBack callBack) {
        this.mCallBack = callBack;
    }
}
