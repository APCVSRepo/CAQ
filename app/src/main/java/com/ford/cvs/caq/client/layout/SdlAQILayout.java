package com.ford.cvs.caq.client.layout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.ford.cvs.caq.client.R;

import java.io.IOException;
import java.io.InputStream;

import static android.graphics.Color.rgb;

/**
 * Created by leon on 2017/4/25.
 */

public abstract class SdlAQILayout
{
	public static class WeatherInfo
	{
		public String city = "";
		public String condition = "";
		public String humidity = "";
		public String temperature = "";
		public String realFeel = "";
		public String pressure = "";
		public String tips = "";
		public String windDir = "";
		public String windLevel = "";
	}


	protected final static int MAX_AQI = 350;
	protected final static int LAYOUT_WIDTH = 770;
	protected final static int LAYOUT_HEIGHT = 250;

	// MEMBER
	protected Context mCtx;
	protected WeatherInfo mLastWeatherInfo = new WeatherInfo();
	protected int mLastInnerAQI = 0;
	protected int mLastOuterAQI = 0;


	// Static Method
	private static byte[] read(Context ctx, int id) throws IOException
	{
		Resources res = ctx.getResources();
		InputStream ios = res.openRawResource(id);
		int count = 0;
		while (ios.read() != -1)
		{
			++count;
		}

		ios.reset();
		byte[] buffer = new byte[count];

		try
		{
			if (ios.read(buffer) == -1)
			{
				throw new IOException("EOF reached while trying to read the whole file");
			}
		}
		finally
		{
			try
			{
				if (ios != null)
					ios.close();
			}
			catch (IOException e)
			{
				Log.e("read", e.getMessage());
			}
		}

		return buffer;
	}

	public static Bitmap loadBMP(Context ctx, int res)
	{
		byte[] data = null;
		try
		{
			data = read(ctx, res);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	public static int getAQIColor(int aqi)
	{
		if (aqi <= 35)
			return rgb(143, 208, 188);
		else if (aqi <= 75)
			return rgb(139, 194, 74);
		else if (aqi <= 115)
			return rgb(223, 196, 45);
		else if (aqi <= 150)
			return rgb(151, 140, 0);
		else if (aqi <= 250)
			return rgb(229, 57, 55);
		else if (aqi <= 350)
			return rgb(97, 79, 131);
		else
			return rgb(60, 60, 52);
	}

	public static int getAQIColorDemo1(int aqi)
	{
		if (aqi <= 35)
			return rgb(0, 181, 100);
		else if (aqi <= 115)
			return rgb(255, 187, 27);
		else if (aqi <= 150)
			return rgb(208, 2, 27);
		else
			return rgb(126, 0, 35);
	}

	public static Bitmap getConditionBmp(Context ctx, String condition)
	{
		if (condition.contains("雷"))
			return loadBMP(ctx, R.drawable.aqi2_thunder);
		else if (condition.contains("雨"))
			return loadBMP(ctx, R.drawable.aqi2_rain);
		else if (condition.contains("多云"))
			return loadBMP(ctx, R.drawable.aqi2_cloudy);
		else if (condition.contains("阴"))
			return loadBMP(ctx, R.drawable.aqi2_overcast);
		else if (condition.contains("晴"))
			return loadBMP(ctx, R.drawable.aqi2_sunny);
		else if (condition.contains("雪"))
			return loadBMP(ctx, R.drawable.aqi2_snow);
		else return loadBMP(ctx, R.drawable.aqi2_fs_alt);
	}

	public static Bitmap roundCornerImg(Bitmap bmp, int pixels)
	{
		Bitmap output = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bmp, rect, rect, paint);
		return output;
	}

	// Override Method
	protected void init(Context ctx)
	{
		mCtx = ctx;
	}


	protected InputStream getAQILayoutImage(int innerAQI, int outerAQI, WeatherInfo wi)
	{
		return null;
	}

	// Interface
	public InputStream updateWeather(WeatherInfo wi)
	{
		mLastWeatherInfo = wi;
		return getAQILayoutImage(mLastInnerAQI, mLastOuterAQI, mLastWeatherInfo);
	}

	public InputStream updateAQI(int innerAQI, int outerAQI)
	{
		mLastInnerAQI = innerAQI;
		mLastOuterAQI = outerAQI;
		return getAQILayoutImage(mLastInnerAQI, mLastOuterAQI, mLastWeatherInfo);
	}

	public InputStream update(int innerAQI, int outerAQI, WeatherInfo wi)
	{
		mLastWeatherInfo = wi;
		mLastInnerAQI = (innerAQI < 0 ? 0 : innerAQI);
		mLastOuterAQI = (outerAQI > MAX_AQI ? MAX_AQI : outerAQI);
		return getAQILayoutImage(mLastInnerAQI, mLastOuterAQI, mLastWeatherInfo);
	}

}
