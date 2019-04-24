package com.ford.cvs.caq.client;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by leon on 2017/3/3.
 */

public class SdlPreferences
{
	private static String PREF_ANME = "data";
	private static String FIELD_BT_ADDR = "bt_device_addr";


	public static void setBTDeviceAddr(Context ctx, String addr)
	{
		SharedPreferences pref = ctx.getSharedPreferences(PREF_ANME, MODE_PRIVATE);

		SharedPreferences.Editor editor = pref.edit();
		editor.putString(FIELD_BT_ADDR, addr);
		editor.commit();
	}


	public static String getBTDeviceAddr(Context ctx)
	{
		SharedPreferences pref = ctx.getSharedPreferences(PREF_ANME, MODE_PRIVATE);

		return pref.getString(FIELD_BT_ADDR, "");
	}
}
