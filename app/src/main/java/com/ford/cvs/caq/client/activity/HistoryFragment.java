package com.ford.cvs.caq.client.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ford.cvs.caq.client.R;
import com.ford.cvs.caq.client.charts.CaqColumnChartView;
import com.ford.cvs.caq.client.charts.CaqColumnData;
import com.ford.cvs.caq.client.widget.DateFilterDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * 历史页面
 * **/
public class HistoryFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private CaqColumnChartView chart = null;
    private RadioButton dayBtn = null;
    private RadioButton weekBtn = null;
    private RadioButton monthBtn = null;
    private RadioGroup btnGroup = null;
    private Button filterBtn = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_history, container, false);
        init(view);
        initListener();
        initData();
        return view;
    }

    private void init(View view){
        chart = (CaqColumnChartView)view.findViewById(R.id.chart);
        btnGroup=(RadioGroup)view.findViewById(R.id.button_group);
        dayBtn = (RadioButton) view.findViewById(R.id.button_day);
        weekBtn = (RadioButton) view.findViewById(R.id.button_week);
        monthBtn = (RadioButton) view.findViewById(R.id.button_month);
        filterBtn = (Button) view.findViewById(R.id.button_filter);
    }

    private void initListener(){
        btnGroup.setOnCheckedChangeListener(this);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFilterDialog dateFilterDialog=new DateFilterDialog(getActivity());
                dateFilterDialog.show();
            }
        });
    }

    private void initData(){
        List<CaqColumnData> data = generateScrollData();
        chart.setData(data);
    }


    private List<CaqColumnData> generateScrollData() {
        List<CaqColumnData> data = new ArrayList<CaqColumnData>();
        int numColumns = 31;
        for (int i = 0; i < numColumns; ++i) {
            CaqColumnData d = new CaqColumnData();
            d.setLabel("12/" + (i+1));
            d.setValue((float) Math.random() * 350f + 5);
            data.add(d);
        }
        return data;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.button_day:
                initData();
                break;
            case R.id.button_week:
                initData();
                break;
            case R.id.button_month:
                initData();
                break;
        }
    }
}
