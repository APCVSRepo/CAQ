package com.ford.cvs.caq.client.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ford.cvs.caq.client.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFilterDialog  extends Dialog implements View.OnClickListener{

    private final int DATE_PICKER_START = 1;
    private final int DATE_PICKER_END = 2;

    private Context ctx;

    private Button startBtn = null;
    private Button endBtn = null;
    private LinearLayout datePickerGroup = null;
    private Button confirmBtn = null;
    private TextView titleView = null;
    private DatePicker datePicker = null;

    private int datePickerType = 0;

    public DateFilterDialog(Context context) {
        super(context);
        this.ctx = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
        initListener();
    }

    private void init(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.date_filter_dialog);

        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.TOP);
        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        lp.width =dm.widthPixels;
        dialogWindow.setAttributes(lp);

    }

    private void initView(){
        startBtn = (Button) findViewById(R.id.start_time_btn);
        endBtn = (Button) findViewById(R.id.end_time_btn);
        confirmBtn = (Button) findViewById(R.id.date_picker_confirm);
        datePickerGroup = (LinearLayout) findViewById(R.id.date_picker_group);
        titleView = (TextView) findViewById(R.id.date_picker_title);
        datePicker = (DatePicker) findViewById(R.id.date_Picker);
    }

    private void initListener(){
        startBtn.setOnClickListener(this);
        endBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_time_btn:
                showDatePicker(DATE_PICKER_START,
                        parseDate(startBtn.getText().toString()),
                        parseDate(endBtn.getText().toString()));
                break;
            case R.id.end_time_btn:
                showDatePicker(DATE_PICKER_END,
                        parseDate(startBtn.getText().toString()),
                        parseDate(endBtn.getText().toString()));
                break;
            case R.id.date_picker_confirm:
                updateDate();
                break;
        }
    }

    private Date parseDate(String dateStr){
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private void showDatePicker(int datePickerType, Date startDate, Date endDate){
        if(startDate == null && endDate == null) return;
        this.datePickerType = datePickerType;
        Calendar calendar = Calendar.getInstance();
        if(datePickerType == DATE_PICKER_START){
            titleView.setText("选择开始日期");
            datePicker.setMinDate(new Date(0).getTime());
            datePicker.setMaxDate(endDate.getTime());
            calendar.setTime(startDate);
        }else if(datePickerType == DATE_PICKER_END){
            titleView.setText("选择结束日期");
            datePicker.setMinDate(startDate.getTime());
            datePicker.setMaxDate(System.currentTimeMillis());
            calendar.setTime(endDate);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.updateDate(year, month, day);
        datePickerGroup.setVisibility(View.VISIBLE);
    }

    private void updateDate(){
        int year = datePicker.getYear();
        int month = datePicker.getMonth() + 1;
        int day = datePicker.getDayOfMonth();
        String dateString = year + "." + String.format("%02d", month) + "." + day;
        if(datePickerType == DATE_PICKER_START){
            startBtn.setText(dateString);
        }else if(datePickerType == DATE_PICKER_END){
            endBtn.setText(dateString);
        }
        datePickerGroup.setVisibility(View.GONE);
    }
}
