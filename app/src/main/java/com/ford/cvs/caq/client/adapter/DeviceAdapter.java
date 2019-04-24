package com.ford.cvs.caq.client.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ford.cvs.caq.client.R;

import java.util.ArrayList;

/**
 * Created by Ivan on 2017/2/17.
 */

public class DeviceAdapter extends BaseAdapter
{
    private ArrayList<BluetoothDevice> mDevices;
    private LayoutInflater mInflater;

    class ViewHolder
    {
        TextView deviceName;
        //TextView deviceAddress;
    }

    public DeviceAdapter(Context context)
    {
        mDevices = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
    }

    public void addDevice(BluetoothDevice device)
    {
        if (!mDevices.contains(device) && device.getName() != null)
            mDevices.add(device);
    }

    @Override
    public int getCount()
    {
        return mDevices.size();
    }

    @Override
    public Object getItem(int i)
    {
        return mDevices.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        ViewHolder holder;
        if(view == null)
        {
            view = mInflater.inflate(R.layout.device_item, null);

            holder = new ViewHolder();
            holder.deviceName = (TextView)view.findViewById(R.id.device_item_name);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();

            final String deviceName = mDevices.get(i).getName();
            if (deviceName != null && !deviceName.isEmpty())
                holder.deviceName.setText(deviceName);
            else
                holder.deviceName.setText(R.string.unknown_device);
        }

        return view;
    }

}
