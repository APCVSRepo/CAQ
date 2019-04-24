package com.ford.cvs.caq.client.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ford.cvs.caq.client.R;
import com.ford.cvs.caq.client.widget.DashboardView;


/**
 * 滤网页面
 * **/
public class FilterScreenFragment extends Fragment
{


    private DashboardView dashboardView;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_filter_screen, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        ((TextView)view.findViewById(R.id.title_tv)).setText("滤网状况");
        dashboardView=(DashboardView) view.findViewById(R.id.dashboardView);
    }

    private void initData(){

       new Thread(){
           @Override
           public void run() {
               for (int i = 0; i <101 ; i++) {
                   try{
                       sleep(50);
                   }catch (Exception e){

                   }
                   mHandler.obtainMessage(0,i,0).sendToTarget();
               }
           }
       }.start();
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int num=msg.arg1;
            dashboardView.setPercent(num);
        }
    };

}
