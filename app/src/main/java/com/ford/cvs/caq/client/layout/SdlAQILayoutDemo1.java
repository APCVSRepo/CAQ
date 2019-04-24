package com.ford.cvs.caq.client.layout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.graphics.ColorUtils;

import com.ford.cvs.caq.client.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by leon on 2017/3/1.
 */

public class SdlAQILayoutDemo1 extends SdlAQILayout
{
	// SINGLETON
	private static SdlAQILayoutDemo1 mInstance = null;
	public static SdlAQILayoutDemo1 getInstance(Context ctx)
	{
		if (mInstance == null)
		{
			mInstance = new SdlAQILayoutDemo1();
			mInstance.init(ctx);
		}
		return mInstance;
	}

	protected SdlAQILayoutDemo1()
	{

	}


	private Bitmap mInnerIndicatorBmp;
	private Bitmap mOuterIndicatorBmp;

	private Typeface mHeitiFont;


	private Paint mTempPaint;
	private Paint mWeatherPaint;
	private Paint mPmInnerTitlePaint;
	private Paint mPmOuterTitlePaint;
	private Paint mPmInnerPaint;
	private Paint mPmOuterPaint;

	private Paint.FontMetricsInt mInnerFontMetrics;
	private Paint.FontMetricsInt mOuterFontMetrics;


	protected void init(Context ctx)
	{
		super.init(ctx);

		// Font
		mHeitiFont = Typeface.createFromAsset(ctx.getAssets(), "font/STHEITI-LIGHT.TTC");

		// Bitmap
		mInnerIndicatorBmp = loadBMP(mCtx, R.drawable.aqi2_inner_indicator);
		mOuterIndicatorBmp = loadBMP(mCtx, R.drawable.aqi2_outer_indicator);


		// Paint
		mTempPaint = new Paint();
		mTempPaint.setTextAlign(Paint.Align.LEFT);
		mTempPaint.setColor(Color.WHITE);
		mTempPaint.setTextSize(38f);
		mTempPaint.setAntiAlias(true);
		mTempPaint.setTypeface(mHeitiFont);

		mWeatherPaint = new Paint();
		mWeatherPaint.setTextAlign(Paint.Align.LEFT);
		mWeatherPaint.setColor(Color.WHITE);
		mWeatherPaint.setTextSize(18f);
		mWeatherPaint.setAntiAlias(true);
		mWeatherPaint.setTypeface(mHeitiFont);

		mPmInnerTitlePaint = new Paint();
		mPmInnerTitlePaint.setTextAlign(Paint.Align.LEFT);
		mPmInnerTitlePaint.setColor(Color.WHITE);
		mPmInnerTitlePaint.setTextSize(20f);
		mPmInnerTitlePaint.setFakeBoldText(true);
		mPmInnerTitlePaint.setAntiAlias(true);
		mPmInnerTitlePaint.setTypeface(mHeitiFont);

		mPmOuterTitlePaint = new Paint();
		mPmOuterTitlePaint.setTextAlign(Paint.Align.LEFT);
		mPmOuterTitlePaint.setColor(Color.WHITE);
		mPmOuterTitlePaint.setTextSize(25f);
		mPmOuterTitlePaint.setFakeBoldText(true);
		mPmOuterTitlePaint.setAntiAlias(true);
		mPmOuterTitlePaint.setTypeface(mHeitiFont);

		mPmInnerPaint = new Paint();
		mPmInnerPaint.setTextAlign(Paint.Align.CENTER);
		mPmInnerPaint.setColor(Color.WHITE);
		mPmInnerPaint.setTextSize(55f);
		mPmInnerPaint.setAntiAlias(true);
		mPmInnerPaint.setTypeface(mHeitiFont);

		mPmOuterPaint = new Paint();
		mPmOuterPaint.setTextAlign(Paint.Align.CENTER);
		mPmOuterPaint.setTextSize(25f);
		mPmOuterPaint.setAntiAlias(true);
		mPmOuterPaint.setTypeface(mHeitiFont);

		mInnerFontMetrics = mPmInnerPaint.getFontMetricsInt();
		mOuterFontMetrics = mPmOuterPaint.getFontMetricsInt();

	}


	protected InputStream getAQILayoutImage(int innerAQI, int outerAQI, WeatherInfo wi)
	{
		Bitmap mainLayoutBmp = Bitmap.createBitmap(LAYOUT_WIDTH, LAYOUT_HEIGHT, Bitmap.Config.ARGB_8888);
		int innerAQIColor = getAQIColorDemo1(innerAQI);
		int outerAQIColor = getAQIColorDemo1(outerAQI);

		mPmOuterPaint.setColor(outerAQIColor);

		Canvas layoutCanvas = new Canvas(mainLayoutBmp);


		// Draw AQI title in circle
		layoutCanvas.drawText("车内PM2.5", 60, 23, mPmInnerTitlePaint);

		Paint paint = new Paint();
		paint.setColor(innerAQIColor);
		paint.setShader(null);
		RectF innerEllipse = new RectF(30, 30, 200, 200);
		layoutCanvas.drawOval(innerEllipse, paint);

		layoutCanvas.drawText(String.format("%03d", innerAQI), 113, 130, mPmInnerPaint);

		layoutCanvas.drawText("CO2", 30, 240, mPmOuterTitlePaint);
		layoutCanvas.drawText("High", 170, 240, mPmOuterPaint);


		layoutCanvas.drawText(wi.city, 405, 55, mWeatherPaint);
		layoutCanvas.drawText(wi.temperature + "°", 400, 100, mTempPaint);

		layoutCanvas.drawText(wi.windDir + wi.windLevel + "级", 400, 125, mWeatherPaint);
		layoutCanvas.drawText(wi.humidity + "%RH", 400, 150, mWeatherPaint);
		layoutCanvas.drawText(wi.pressure + "hPa", 400, 175, mWeatherPaint);


		//show img on phone
//		try
//		{
//			final String filePathName = "/sdcard/test.png";
//			File myCaptureFile = new File(filePathName);
//
//			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
//			mainLayoutBmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
//			bos.flush();
//			bos.close();
//
//			// show image
//			Intent intent = new Intent("android.intent.action.VIEW");
//			intent.addCategory("android.intent.category.DEFAULT");
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			Uri uri = Uri.fromFile(myCaptureFile);
//			intent.setDataAndType(uri, "image/*");
//			mCtx.startActivity(intent);
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}


		// convert to inputstream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		mainLayoutBmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return new ByteArrayInputStream(baos.toByteArray());
	}

}
