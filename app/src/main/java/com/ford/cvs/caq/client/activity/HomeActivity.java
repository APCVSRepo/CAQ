package com.ford.cvs.caq.client.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.ford.cvs.caq.client.R;
import com.ford.cvs.caq.client.SdlPreferences;
import com.ford.cvs.caq.client.SdlProxyService;

public class HomeActivity extends AppCompatActivity implements OnCheckedChangeListener
{
	private FragmentManager mFragmentManager;
	private RadioGroup menu_panel_rg;
	private RadioButton real_time_rb;
	private RadioButton history_rb;
	private RadioButton filter_screen_rb;
	private String mCurrentFragmentTag = null;
	private SdlProxyService.ServiceBinder mBinder;



	private ServiceConnection mSrvConn = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			mBinder = (SdlProxyService.ServiceBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mBinder = null;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		init();
	}

	@Override
	protected void onDestroy()
	{
		release();
		super.onDestroy();
	}

	private void init()
	{
		mFragmentManager = getSupportFragmentManager();
		menu_panel_rg = (RadioGroup) findViewById(R.id.menu_panel_rg);
		real_time_rb = (RadioButton) findViewById(R.id.real_time_rb);
		history_rb = (RadioButton) findViewById(R.id.history_rb);
		filter_screen_rb = (RadioButton) findViewById(R.id.filter_screen_rb);

		menu_panel_rg.setOnCheckedChangeListener(this);
		//menu_panel_rg.check(R.id.real_time_rb);


		// switch on real time fragment, which depend on whether have address or not.
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		String btAddr = SdlPreferences.getBTDeviceAddr(this);
		if (btAddr == null || btAddr.isEmpty())
		{
			mCurrentFragmentTag = DeviceFragment.class.getSimpleName();
			fragmentTransaction.add(R.id.home_main, new DeviceFragment(), mCurrentFragmentTag);
		}
		else
		{
			mCurrentFragmentTag = RealtimeFragment.class.getSimpleName();
			fragmentTransaction.add(R.id.home_main, new RealtimeFragment(), mCurrentFragmentTag);
		}
		fragmentTransaction.commit();



		Intent intent = new Intent(this, SdlProxyService.class);
		bindService(intent, mSrvConn, BIND_AUTO_CREATE);
	}


	private void release()
	{
		unbindService(mSrvConn);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		String tag = null;
		Fragment fragment = null;
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		Fragment currentFragment = mFragmentManager.findFragmentByTag(mCurrentFragmentTag);
		if (currentFragment != null)
		{
			fragmentTransaction.hide(currentFragment);
		}
		switch (checkedId)
		{
		case R.id.real_time_rb:
			tag = DeviceFragment.class.getSimpleName();
			Fragment menu1 = mFragmentManager.findFragmentByTag(tag);
			if (menu1 == null)
			{
				tag = RealtimeFragment.class.getSimpleName();
				menu1 = mFragmentManager.findFragmentByTag(tag);
				if (menu1 != null)
					fragment = menu1;
				else
					fragment = new DeviceFragment();
			}
			else
			{
				fragment = menu1;
			}
			break;
		case R.id.history_rb:
			tag = HistoryFragment.class.getSimpleName();
			Fragment menu2 = mFragmentManager.findFragmentByTag(tag);
			if (menu2 != null)
			{
				fragment = menu2;
			}
			else
			{
				fragment = new HistoryFragment();
			}
			break;
		case R.id.filter_screen_rb:
			tag = FilterScreenFragment.class.getSimpleName();
			Fragment menu3 = mFragmentManager.findFragmentByTag(tag);
			if (menu3 != null)
			{
				fragment = menu3;
			}
			else
			{
				fragment = new FilterScreenFragment();
			}
			break;
		}
		if (fragment != null && fragment.isAdded())
		{
			fragmentTransaction.show(fragment);
		}
		else
		{
			fragmentTransaction.add(R.id.home_main, fragment, tag);
		}
		fragmentTransaction.commit();
		mCurrentFragmentTag = tag;
	}

	public SdlProxyService.ServiceBinder getServiceBinder()
	{
		return mBinder;
	}

	public void realtimeFragment()
	{
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction
				.replace(R.id.home_main, new RealtimeFragment(), RealtimeFragment.class.getSimpleName());
		fragmentTransaction.commit();

	}

	public void deviceFragment()
	{
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.home_main, new DeviceFragment(), DeviceFragment.class.getSimpleName());
		fragmentTransaction.commit();

	}
}
