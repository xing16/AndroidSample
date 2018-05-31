package com.xing.androidsample.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.xing.androidsample.R;

import java.util.ArrayList;
import java.util.List;

public class CustomViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view);
        LockView lockView = findViewById(R.id.lock_view);
        List<Integer> intList = new ArrayList<>();
        intList.add(3);
        intList.add(7);
        intList.add(4);
        intList.add(2);
        lockView.setStandard(intList);
        lockView.setOnDrawCompleteListener(new LockView.OnDrawCompleteListener() {
            @Override
            public void onComplete(boolean isSuccess) {
                Toast.makeText(CustomViewActivity.this, isSuccess ? "success" : "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static void start(Context context) {
        Intent intent = new Intent(context, CustomViewActivity.class);
        context.startActivity(intent);
    }
}
