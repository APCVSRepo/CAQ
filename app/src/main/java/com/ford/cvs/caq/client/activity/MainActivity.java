package com.ford.cvs.caq.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ford.cvs.caq.client.R;
import com.ford.cvs.caq.client.adapter.SmallAppAdapter;
import com.ford.cvs.caq.client.layout.SdlAQILayout;
import com.ford.cvs.caq.client.layout.SdlAQILayoutDemo1;
import com.ford.cvs.caq.client.model.DownLoadProgressInfo;
import com.ford.cvs.caq.client.widget.ActionItem;
import com.ford.cvs.caq.client.widget.QuickAction;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int STATUS_ITEM1=1;
    private static final int STATUS_ITEM2=2;
    private static final int STATUS_ITEM3=3;
    private static final int CATEGORY_ITEM1=4;
    private static final int CATEGORY_ITEM2=5;
    private static final int CATEGORY_ITEM3=6;
    private QuickAction status_quickAction;
    private QuickAction category_quickAction;
    private ListView small_app_lv;
    private TextView status_tv;
    private TextView category_tv;
    private SmallAppAdapter smallAppAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initListener();
        initData();

    }

    private void init(){
        small_app_lv=(ListView)findViewById(R.id.small_app_lv);
        status_tv=(TextView)findViewById(R.id.status_tv);
        category_tv=(TextView) findViewById(R.id.category_tv);
        status_quickAction = new QuickAction(this, QuickAction.VERTICAL);
        category_quickAction= new QuickAction(this, QuickAction.VERTICAL);
    }

    private void initListener(){
        status_tv.setOnClickListener(this);
        category_tv.setOnClickListener(this);
        status_quickAction.setOnActionItemClickListener(statusOnActionItemClickListener);
        category_quickAction.setOnActionItemClickListener(categoryOnActionItemClickListener);

    }

    private QuickAction.OnActionItemClickListener statusOnActionItemClickListener=new QuickAction.OnActionItemClickListener() {
        @Override
        public void onItemClick(QuickAction source, int pos, int actionId) {
            ActionItem actionItem = status_quickAction.getActionItem(pos);
            status_tv.setText(actionItem.getTitle());
        }
    };

    private QuickAction.OnActionItemClickListener categoryOnActionItemClickListener=new QuickAction.OnActionItemClickListener() {
        @Override
        public void onItemClick(QuickAction source, int pos, int actionId) {
            ActionItem actionItem = category_quickAction.getActionItem(pos);
            category_tv.setText(actionItem.getTitle());
        }
    };

    private void initData(){
        List<DownLoadProgressInfo> list=new ArrayList<>();
        for (int i = 0; i <2; i++) {
            DownLoadProgressInfo dwo=new DownLoadProgressInfo();
            dwo.setName("福特小程序"+(i+1));
            dwo.setProgress(0);
            list.add(dwo);
        }
        ActionItem statusItem1= new ActionItem(STATUS_ITEM1, "全部", null);
        ActionItem statusItem2 = new ActionItem(STATUS_ITEM2, "已下载", null);
        ActionItem statusItem3= new ActionItem(STATUS_ITEM3, "未下载", null);
        ActionItem categoryItem1= new ActionItem(CATEGORY_ITEM1, "全部", null);
        ActionItem categoryItem2= new ActionItem(CATEGORY_ITEM2, "AppLink应用", null);
        ActionItem categoryItem3= new ActionItem(CATEGORY_ITEM3, "非AppLink应用", null);
        status_quickAction.addActionItem(statusItem1);
        status_quickAction.addActionItem(statusItem2);
        status_quickAction.addActionItem(statusItem3);
        category_quickAction.addActionItem(categoryItem1);
        category_quickAction.addActionItem(categoryItem2);
        category_quickAction.addActionItem(categoryItem3);
        smallAppAdapter=new SmallAppAdapter(this, list);
        small_app_lv.setAdapter(smallAppAdapter);
    }

    public void updateProgress( final int position){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 101; i++) {
                    handler.obtainMessage(0,i,position).sendToTarget();
                    try {
                        Thread.sleep(50);

                    }catch (Exception e){

                    }
                }
            }
        }).start();
    }

    public void openHome(){
        startActivity(new Intent(this, HomeActivity.class));
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            smallAppAdapter.upDateProgress(msg.arg2,msg.arg1);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.status_tv:
                status_quickAction.show(v);
                break;
            case R.id.category_tv:
                category_quickAction.show(v);
                break;
        }
    }
}
