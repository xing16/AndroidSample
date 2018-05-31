package com.xing.androidsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.xing.androidsample.recyclerview.RecyclerViewActivity;
import com.xing.androidsample.view.CustomViewActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_recycler_view:
                RecyclerViewActivity.start(this);
                break;
            case R.id.btn_custom_view:
                CustomViewActivity.start(this);
                break;
        }
    }
}
