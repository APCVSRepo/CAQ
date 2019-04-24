package com.ford.cvs.caq.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ford.cvs.caq.client.R;
import com.ford.cvs.caq.client.activity.MainActivity;
import com.ford.cvs.caq.client.model.DownLoadProgressInfo;

import java.util.List;

/**
 * Created by Ivan on 2017/2/17.
 */

public class SmallAppAdapter extends BaseAdapter
{
    private List<DownLoadProgressInfo> mList;
    private LayoutInflater layoutInflater;
    private Context mContext;

    public SmallAppAdapter(Context context, List<DownLoadProgressInfo> list){
        mContext=context;
        layoutInflater= LayoutInflater.from(context);
        mList=list;
    }

    @Override
    public int getCount() {
        return mList==null?0:mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final DownLoadProgressInfo downLoadProgressInfo=mList.get(position);
        if(convertView==null){
            convertView=layoutInflater.inflate(R.layout.samll_app_item, null);
        }
        TextView textView=(TextView) convertView.findViewById(R.id.downLoad_tv);
        ProgressBar dwonload_progress_pb=(ProgressBar) convertView.findViewById(R.id.dwonload_progress_pb);
        dwonload_progress_pb.setProgress(downLoadProgressInfo.getProgress());
        textView.setText(downLoadProgressInfo.getStatus());
        if(downLoadProgressInfo.getProgress()==100){
            textView.setCompoundDrawablesWithIntrinsicBounds(null, mContext.getResources().getDrawable(R.mipmap.open_icon), null, null);
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!downLoadProgressInfo.isDownloading()){
                    if(downLoadProgressInfo.getProgress()==100){
                        MainActivity mainActivity=(MainActivity) mContext;
                        mainActivity.openHome();
                    }else{
                        MainActivity mainActivity=(MainActivity) mContext;
                        mainActivity.updateProgress(position);
                    }
                }
            }
        });

        return convertView;
    }

    public void upDateProgress(int position,int progress){
        DownLoadProgressInfo downLoadProgressInfo=mList.get(position);
        downLoadProgressInfo.setProgress(progress);
        downLoadProgressInfo.setDownloading(true);
        if(progress==100){
            downLoadProgressInfo.setStatus("打开");
            downLoadProgressInfo.setDownloading(false);
        }
        this.notifyDataSetChanged();
    }

    public DownLoadProgressInfo getDownLoadInfo(int position){
        return  mList.get(position);
    }
}
