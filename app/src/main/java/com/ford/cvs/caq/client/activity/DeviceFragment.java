package com.ford.cvs.caq.client.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ford.cvs.caq.client.R;
import com.ford.cvs.caq.client.SdlPreferences;
import com.ford.cvs.caq.client.adapter.DeviceAdapter;


/**
 * 实时页面
 **/
public class DeviceFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener
{
	private static final int SCAN_TIMEOUT = 30000;
	private ListView device_lv;
	private LinearLayout search_fail_layout;
	private FrameLayout search_btn;
	private TextView search_label_tv;
	private ImageView search_device_bg;
	private Animation animation;


	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothLeScanner mBluetoothScanner;
	private DeviceAdapter mDeviceAdapter;

	private enum UIType
	{
		UIT_SEARCHING,
		UIT_SEARCH_SUCCESS,
		UIT_SEARCH_FAIL,
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_device, container, false);
		init(view);
		return view;
	}

	private void init(View view)
	{
		device_lv = (ListView) view.findViewById(R.id.device_lv);
		search_fail_layout = (LinearLayout) view.findViewById(R.id.search_fail_layout);
		search_btn = (FrameLayout) view.findViewById(R.id.search_btn);
		search_label_tv = (TextView) view.findViewById(R.id.search_label_tv);
		search_device_bg = (ImageView) view.findViewById(R.id.search_device_bg);

		animation = AnimationUtils.loadAnimation(getActivity(), R.anim.circle);
		animation.setInterpolator(new LinearInterpolator());

		device_lv.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.device_title, null));

		search_btn.setOnClickListener(this);
		device_lv.setOnItemClickListener(this);


		final BluetoothManager bluetoothManager =
				(BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();

	}


	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.search_btn:
			if (search_label_tv.getVisibility() == View.VISIBLE)
			{
				if (scanDevice(true))
				{
					// start searching
					switchUIType(UIType.UIT_SEARCHING);

					new Handler().postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							// Search finish
							if (mDeviceAdapter.getCount() > 0)
								switchUIType(UIType.UIT_SEARCH_SUCCESS);
							else
								switchUIType(UIType.UIT_SEARCH_FAIL);
						}
					}, SCAN_TIMEOUT);
				}
				else
				{
					// BT is off
					switchUIType(UIType.UIT_SEARCH_FAIL);
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		if (position > 0)
		{
			scanDevice(false);

			HomeActivity homeActivity = (HomeActivity) getActivity();

			// Save address
			String address = ((BluetoothDevice)mDeviceAdapter.getItem(position - 1)).getAddress();
			SdlPreferences.setBTDeviceAddr(homeActivity, address);

			homeActivity.realtimeFragment();
		}
	}


	private void switchUIType(UIType type)
	{
		switch (type)
		{
		case UIT_SEARCHING:
			search_device_bg.startAnimation(animation);
			search_label_tv.setVisibility(View.GONE);
			search_fail_layout.setVisibility(View.GONE);
			device_lv.setVisibility(View.VISIBLE);
			break;
		case UIT_SEARCH_SUCCESS:
			search_device_bg.clearAnimation();
			search_label_tv.setVisibility(View.VISIBLE);
			search_fail_layout.setVisibility(View.GONE);
			device_lv.setVisibility(View.VISIBLE);
			break;
		case UIT_SEARCH_FAIL:
			search_device_bg.clearAnimation();
			search_label_tv.setVisibility(View.VISIBLE);
			search_fail_layout.setVisibility(View.VISIBLE);
			device_lv.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}


	private boolean scanDevice(final boolean scan)
	{
		// Return false when BT switch off / current already stop /start to scan
		if (!mBluetoothAdapter.isEnabled() || scan == mBluetoothAdapter.isDiscovering())
			return false;

		if (scan)
		{
			// Stops scanning after a pre-defined scan period.
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					mBluetoothScanner.stopScan(mScanCallback);
				}
			}, SCAN_TIMEOUT);

			// clear
			mDeviceAdapter = new DeviceAdapter(getActivity());
			device_lv.setAdapter(mDeviceAdapter);
			mDeviceAdapter.notifyDataSetChanged();

			if (mBluetoothScanner == null)
				mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();

			mBluetoothScanner.startScan(mScanCallback);
		}
		else
		{
			mBluetoothScanner.stopScan(mScanCallback);
		}

		return true;
	}

	// Device scan callback.
	private ScanCallback mScanCallback = new ScanCallback()
	{
		@Override
		public void onScanResult(int callbackType, ScanResult result)
		{
			mDeviceAdapter.addDevice(result.getDevice());
			mDeviceAdapter.notifyDataSetChanged();

			super.onScanResult(callbackType, result);
		}
	};


}
