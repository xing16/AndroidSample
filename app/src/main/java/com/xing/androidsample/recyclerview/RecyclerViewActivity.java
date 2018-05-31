package com.xing.androidsample.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xing.androidsample.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RecyclerViewActivity extends AppCompatActivity {

    private List<String> dataList = new ArrayList();

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        recyclerView = findViewById(R.id.recycler_view);
        initData();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerAdapter adapter = new RecyclerAdapter(this, dataList);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, new DividerItemDecoration.OnGroupListener() {
            @Override
            public String getGroupName(int position) {
                return dataList.get(position).substring(0, 1);
            }
        }));

    }

    private void initData() {
        dataList.add("java");
        dataList.add("jdk");
        dataList.add("php");
        dataList.add("c++");
        dataList.add("linux");
        dataList.add("windows");
        dataList.add("macos");
        dataList.add("red hat");
        dataList.add("python");
        dataList.add("jvm");
        dataList.add("wechat");
        dataList.add("cellphone");
        dataList.add("iphone");
        dataList.add("mouse");
        dataList.add("huawei");
        dataList.add("xiaomi");
        dataList.add("meizu");
        dataList.add("mocrosoft");
        dataList.add("google");
        dataList.add("whatsapp");
        dataList.add("iMac");
        dataList.add("c#");
        dataList.add("iOS");
        dataList.add("water");
        dataList.add("xiaohongshu");
        dataList.add("jake");
        dataList.add("zuk");


        Collections.sort(dataList);

    }


    public static void start(Context context) {
        Intent intent = new Intent(context, RecyclerViewActivity.class);
        context.startActivity(intent);
    }
}
