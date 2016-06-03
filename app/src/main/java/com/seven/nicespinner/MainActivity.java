package com.seven.nicespinner;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {

    NiceSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (NiceSpinner)findViewById(R.id.spinner);
        ArrayList<String> list = new ArrayList<String>();
        Collections.addAll(list, getResources().getStringArray(R.array.array));
        spinner.setDataList(list);
    }
}
