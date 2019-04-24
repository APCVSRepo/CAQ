package com.ford.cvs.caq.client.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ford.cvs.caq.client.R;
import com.ford.cvs.caq.client.layout.SdlAQILayout;
import com.ford.cvs.caq.client.layout.SdlAQILayoutDemo1;
import com.ford.cvs.caq.client.layout.SdlAQILayoutMeter;
import com.ford.cvs.caq.client.SdlPreferences;
import com.ford.cvs.caq.client.SdlProxyService;
import com.ford.cvs.caq.client.data.DataReaderListener;
import com.ford.cvs.caq.client.data.EnvData;
import com.ford.cvs.caq.client.data.airburg.AirburgReader;
import com.ford.cvs.caq.client.data.moji.MojiPubDataReader;
import com.ford.cvs.caq.client.widget.PmTipDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 实时页面
 **/
public class RealtimeFragment extends Fragment implements View.OnClickListener
{
	private static final int MSG_UPDATE_UI = 100;
	private static final int MSG_SHOW_ERROR = 101;

	private ImageView tip_iv;
	private ImageView search_device_iv;
	private TextView temperature_tv;
	private TextView left_weather_tv;
	private TextView right_weather_tv;
	private TextView inner_aqi_tv;
	private TextView outer_aqi_tv;

	private HomeActivity mHomeActivity;
	private AirburgReader mInnerReader;
	private MojiPubDataReader mOuterReader;
	private Timer mTimer;


	private int mInnerAQI = 0;
	private int mOuterAQI = 0;
	private int mInnerThreshold = 35;
	private int mOuterThreshold = 50;

	private boolean mInnerAlertPopup = false;
	private boolean mInnerPurifiedOpen = false;

	private SdlAQILayoutMeter.WeatherInfo mWeatherInfo = null;


	private Handler mUIHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case MSG_UPDATE_UI:
				SdlProxyService.ServiceBinder binder = mHomeActivity.getServiceBinder();

				if (mWeatherInfo != null)
				{
					if (binder != null && binder.ready())
						binder.update(mInnerAQI, mOuterAQI, mWeatherInfo);

					temperature_tv.setText(mWeatherInfo.temperature);
					left_weather_tv.setText(mWeatherInfo.windDir + mWeatherInfo.windLevel + "级\n" +
											mWeatherInfo.humidity + "%RH");
					right_weather_tv.setText(mWeatherInfo.city + " " + mWeatherInfo.condition + "\n" +
											 mWeatherInfo.pressure + "hPa");
				}
				else
				{
					if (binder != null && binder.ready())
						binder.updateAQI(mInnerAQI, mOuterAQI);
				}

				//inner_aqi_tv.setTextColor(Utils.getAQIColor(mInnerAQI));
				inner_aqi_tv.setText(String.valueOf(mInnerAQI));

				outer_aqi_tv.setTextColor(SdlAQILayoutMeter.getAQIColor(mOuterAQI));
				outer_aqi_tv.setText(String.valueOf(mOuterAQI));

				break;
			case MSG_SHOW_ERROR:
				Toast.makeText(mHomeActivity, (String)msg.obj, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}
	};


	private class UpdateAQITask extends TimerTask
	{

		public UpdateAQITask()
		{

		}

		@Override
		public void run()
		{
//			Logic:
//			设 内部阀值为X； 外部阀值为Y
//			1. 当内部AQI大于X并且外部AQI小于Y，开窗或开启外循环
//			2. 当内部AQI大于X并且外部AQI大于Y，打开空调
//			3. 打开空调或外循环后，当内部AQI小于 2/3 X 时提示『空气已净化』

			mUIHandler.sendEmptyMessage(MSG_UPDATE_UI);

			SdlProxyService.ServiceBinder binder = mHomeActivity.getServiceBinder();
			if (binder != null && binder.ready())
			{
				// Inner AQI greater than 60
				if (!mInnerAlertPopup)
				{
					if (mInnerAQI >= mInnerThreshold)
					{
						mInnerAlertPopup = true;
						mInnerPurifiedOpen = true;
						if (mOuterAQI < mOuterThreshold)
							binder.AQIInnerAlert(false);
						else
							binder.AQIInnerAlert(true);
					}
				}
				else
				{
					if (mInnerAQI < mInnerThreshold)
						mInnerAlertPopup = false;
				}


				if ((mInnerAQI < mInnerThreshold * 2.0f / 3.0f) && mInnerPurifiedOpen)
				{
					mInnerPurifiedOpen = false;
					binder.AQIInnerPurifiedAlert();
				}


			}

		}
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
		View view = inflater.inflate(R.layout.fragment_real_time, container, false);
		init(view);
		return view;
	}

	@Override
	public void onDestroy()
	{
		mTimer.cancel();
		mInnerReader.cancel();
		mOuterReader.cancel();

		super.onDestroy();
	}


	private void init(View view)
	{
		tip_iv = (ImageView) view.findViewById(R.id.tip_iv);
		search_device_iv = (ImageView) view.findViewById(R.id.search_device_iv);
		temperature_tv = (TextView) view.findViewById(R.id.temperature_tv);
		left_weather_tv = (TextView) view.findViewById(R.id.left_weather_tv);
		right_weather_tv = (TextView) view.findViewById(R.id.right_weather_tv);
		inner_aqi_tv = (TextView) view.findViewById(R.id.inner_aqi_tv);
		outer_aqi_tv = (TextView) view.findViewById(R.id.outer_aqi_tv);


		tip_iv.setOnClickListener(this);
		search_device_iv.setOnClickListener(this);

		mHomeActivity = (HomeActivity) getActivity();


		// Inner Reader
		mInnerReader = new AirburgReader(mHomeActivity, SdlPreferences.getBTDeviceAddr(mHomeActivity),
										 new DataReaderListener()
		{
			public void onNewData(Object dataObj)
			{
				mInnerAQI = (int) ((EnvData) dataObj).getPm25();
				if (mInnerAQI < 0)
					mInnerAQI = 0;
			}


			public void onError(String msg)
			{
				Message message = new Message();
				message.obj = msg;
				message.what = MSG_SHOW_ERROR;

				mUIHandler.sendMessage(message);
			}
		});
		mInnerReader.start();

		// Outer Reader
		mOuterReader = new MojiPubDataReader(new DataReaderListener()
		{
			@Override
			public void onNewData(Object dataObj)
			{
				try
				{
					JSONObject obj =
							(new JSONObject((String) dataObj)).getJSONObject("data").getJSONObject("condition");

					mWeatherInfo = new SdlAQILayoutMeter.WeatherInfo();
					mWeatherInfo.humidity = obj.getString("humidity");
					mWeatherInfo.temperature = obj.getString("temp");
					mWeatherInfo.realFeel = obj.getString("realFeel");
					mWeatherInfo.pressure = obj.getString("pressure");
					mWeatherInfo.tips = obj.getString("tips");
					mWeatherInfo.windDir = obj.getString("windDir");
					mWeatherInfo.windLevel = obj.getString("windLevel");
					mWeatherInfo.condition = obj.getString("condition");

					obj = (new JSONObject((String) dataObj)).getJSONObject("data").getJSONObject("city");
					mWeatherInfo.city = obj.getString("name");

					obj = (new JSONObject((String) dataObj)).getJSONObject("data").getJSONObject("aqi");
					mOuterAQI = obj.getInt("value");

				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String msg)
			{
			}
		});
		mOuterReader.start();

		mTimer = new Timer();
		mTimer.schedule(new UpdateAQITask(), 0, 3000);

	}


	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.tip_iv:
			PmTipDialog pmTipDialog = new PmTipDialog(getActivity());
			pmTipDialog.show();
			break;
		case R.id.search_device_iv:
			HomeActivity homeActivity = (HomeActivity) getActivity();
			homeActivity.deviceFragment();
			break;
		}
	}
}
