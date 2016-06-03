# NiceSpinner简介


**NiceSpinner**自定义Android Spinner，主要是用Layout、TextView、ImageView结合
### 特点概述：
- **添加数据** ：需要使用setDataList方法，将数据塞入List集合
- **数据回调** ：

  ##### 代码块
```java

   private class SpinnerCallback implements NiceSpinner.NiceSpinnerCallBack {

       @Override
       public void loadData(int moreCount, View view) {

       }
       @Override
       public void setText(String text, View view) {
       }
   }

   spinner.addCallBack(new SpinnerCallback());
```

  ##### 代码分析
   loadData：主要用于Spinner内置的ListView加载下拉数据;

   setText：主要用于在选中item时，事件的处理;
- **其它方法** ：

   spinner.setText();设置Spinner的默认显示内容

   spinner.setSpinnerListHeight();下拉item列表的高度

   spinner.setDataCount(52, DEFAULT_COUNT);//设置显示数据的条数

- **跑马灯效果**:

   在Spinner的text过长时，显示不全所有内容，会出现跑马灯效果

## 反馈与建议
- 邮箱：<seven2qin@gmail.com>
