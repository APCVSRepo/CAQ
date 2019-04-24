package com.ford.cvs.caq.client.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.Typeface;

import com.ford.cvs.caq.client.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by leon on 2017/3/1.
 */

public class SdlAQILayoutMeter extends SdlAQILayout
{
	// SINGLETON
	private static SdlAQILayoutMeter mInstance = null;
	public static SdlAQILayoutMeter getInstance(Context ctx)
	{
		if (mInstance == null)
		{
			mInstance = new SdlAQILayoutMeter();
			mInstance.init(ctx);
		}
		return mInstance;
	}

	protected SdlAQILayoutMeter()
	{

	}


	private final float MIN_POINTER_ROTATE = 9f;
	private final float MAX_POINTER_ROTATE = 171F;
	private final Point mOriginPt = new Point(377, 244);
	private final int MAX_AQI = 350;
	private final Point[] SCALE_POINT =
			{
					new Point(165, 230),
					new Point(193, 155),
					new Point(247, 95),
					new Point(320, 60),
					new Point(405, 60),
					new Point(475, 95),
					new Point(530, 155),
					new Point(560, 230),
			};

	private Typeface mDroidSansFont;
	private Typeface mChineseFont;
	private Typeface mLcdFont;

	private Bitmap mMainLayoutBmp;
	private Bitmap mPmLayoutBmp;
	private Bitmap mPmInnerPointerBmp;
	private Bitmap mPmOuterPointerBmp;


	private Paint mScalePaint;
	private Paint mTempPaint;
	private Paint mWeatherPaint;
	private Paint mPmTitlePaint;
	private Paint mPmMarkPaint;
	private Paint mPmInnerPaint;
	private Paint mPmOuterPaint;


	protected void init(Context ctx)
	{
		super.init(ctx);

		// Font
		mDroidSansFont = Typeface.createFromAsset(ctx.getAssets(), "font/DroidSansFallback.ttf");
		mChineseFont = Typeface.createFromAsset(ctx.getAssets(), "font/FZLTCXHJW.ttf");
		mLcdFont = Typeface.createFromAsset(ctx.getAssets(), "font/LiquidCrystal-Normal.otf");

		// Bitmap
		mMainLayoutBmp = loadBMP(mCtx, R.drawable.aqi_main_layout);
		mPmLayoutBmp = loadBMP(mCtx, R.drawable.aqi_pm_layout);
		mPmInnerPointerBmp = loadBMP(mCtx, R.drawable.aqi_pm_inner_pointer);
		mPmOuterPointerBmp = loadBMP(mCtx, R.drawable.aqi_pm_outer_pointer);


		// Paint
		mScalePaint = new Paint();
		mScalePaint.setTextAlign(Paint.Align.LEFT);
		mScalePaint.setColor(Color.YELLOW);
		mScalePaint.setTextSize(18f);
		mScalePaint.setAntiAlias(true);
		mScalePaint.setTypeface(mDroidSansFont);

		mTempPaint = new Paint();
		mTempPaint.setTextAlign(Paint.Align.LEFT);
		mTempPaint.setColor(Color.WHITE);
		mTempPaint.setTextSize(42f);
		mTempPaint.setAntiAlias(true);
		mTempPaint.setTypeface(mChineseFont);
		mTempPaint.setFakeBoldText(true);

		mWeatherPaint = new Paint();
		mWeatherPaint.setTextAlign(Paint.Align.LEFT);
		mWeatherPaint.setColor(Color.WHITE);
		mWeatherPaint.setTextSize(16f);
		mWeatherPaint.setAntiAlias(true);
		mWeatherPaint.setTypeface(mDroidSansFont);

		mPmTitlePaint = new Paint();
		mPmTitlePaint.setTextAlign(Paint.Align.LEFT);
		mPmTitlePaint.setColor(Color.LTGRAY);
		mPmTitlePaint.setTextSize(18f);
		mPmTitlePaint.setFakeBoldText(true);
		mPmTitlePaint.setAntiAlias(true);
		mPmTitlePaint.setTypeface(mDroidSansFont);

		mPmMarkPaint = new Paint();
		mPmMarkPaint.setTextAlign(Paint.Align.LEFT);
		mPmMarkPaint.setColor(Color.DKGRAY);
		mPmMarkPaint.setTextSize(13f);
		mPmMarkPaint.setFakeBoldText(true);
		mPmMarkPaint.setAntiAlias(true);
		mPmMarkPaint.setTypeface(mDroidSansFont);

		mPmInnerPaint = new Paint();
		mPmInnerPaint.setTextAlign(Paint.Align.LEFT);
		mPmInnerPaint.setTextSize(35f);
		mPmInnerPaint.setAntiAlias(true);
		mPmInnerPaint.setTypeface(mLcdFont);

		mPmOuterPaint = new Paint();
		mPmOuterPaint.setTextAlign(Paint.Align.LEFT);
		mPmOuterPaint.setTextSize(35f);
		mPmOuterPaint.setAntiAlias(true);
		mPmOuterPaint.setTypeface(mLcdFont);

	}


	protected InputStream getAQILayoutImage(int innerAQI, int outerAQI, WeatherInfo wi)
	{
		Bitmap mainLayoutBmp = Bitmap.createBitmap(LAYOUT_WIDTH, LAYOUT_HEIGHT, Bitmap.Config.ARGB_8888);

		Canvas layoutCanvas = new Canvas(mainLayoutBmp);
		layoutCanvas.drawBitmap(mMainLayoutBmp, 0, 0, null);

		for (int i = 0; i < SCALE_POINT.length; i++)
		{
			layoutCanvas.drawText(String.format("%d", MAX_AQI / (SCALE_POINT.length - 1) * i),
								  SCALE_POINT[i].x, SCALE_POINT[i].y, mScalePaint);
		}

		if (wi.temperature.length() > 2)
			layoutCanvas.drawText(wi.temperature + "°", 30, 103, mTempPaint);
		else
			layoutCanvas.drawText(wi.temperature + "°", 40, 103, mTempPaint);

		layoutCanvas.drawText(wi.windDir + "" + wi.windLevel + "级", 118, 80, mWeatherPaint);
		layoutCanvas.drawText(wi.humidity + "%RH", 118, 105, mWeatherPaint);

		layoutCanvas.drawText(wi.city + " " + wi.condition, 585, 80, mWeatherPaint);
		layoutCanvas.drawText(wi.pressure + "hPa", 585, 105, mWeatherPaint);


		Bitmap pmInnerPointerBmp = getPointerBmp(mPmInnerPointerBmp, innerAQI);
		Bitmap pmOuterPointerBmp = getPointerBmp(mPmOuterPointerBmp, outerAQI);
		Bitmap pmLayoutBmp = getPmBmp(innerAQI, outerAQI);


		layoutCanvas.drawBitmap(pmOuterPointerBmp, mOriginPt.x - pmOuterPointerBmp.getWidth() / 2,
								mMainLayoutBmp.getHeight() - pmOuterPointerBmp.getHeight(), null);
		layoutCanvas.drawBitmap(pmInnerPointerBmp, mOriginPt.x - pmInnerPointerBmp.getWidth() / 2,
								mMainLayoutBmp.getHeight() - pmInnerPointerBmp.getHeight(), null);
		layoutCanvas.drawBitmap(pmLayoutBmp, mOriginPt.x - pmLayoutBmp.getWidth() / 2,
								mMainLayoutBmp.getHeight() - pmLayoutBmp.getHeight(), null);

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
//
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


	private Bitmap getPointerBmp(Bitmap pointerBmp, int aqi)
	{
		int drawAQI = aqi;
		if (drawAQI < 0)
			drawAQI = 0;
		else if (drawAQI > MAX_AQI)
			drawAQI = MAX_AQI;


		Bitmap bmp = Bitmap.createBitmap(pointerBmp.getWidth() * 2, pointerBmp.getWidth(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

		Matrix matrix = new Matrix();
		matrix.postRotate((MAX_POINTER_ROTATE - MIN_POINTER_ROTATE) / MAX_AQI * drawAQI + MIN_POINTER_ROTATE,
						  bmp.getWidth() / 2, pointerBmp.getHeight() / 2);
		matrix.postTranslate(0, bmp.getHeight());

		canvas.drawBitmap(pointerBmp, matrix, null);

		return bmp;
	}

	private Bitmap getPmBmp(int innerAQI, int outerAQI)
	{
		mPmInnerPaint.setColor(getAQIColor(innerAQI));
		mPmOuterPaint.setColor(getAQIColor(outerAQI));

		Bitmap bmp = Bitmap.createBitmap(mPmLayoutBmp.getWidth(), mPmLayoutBmp.getHeight(),
										 Bitmap.Config.ARGB_8888);

		Canvas pmCanvas = new Canvas(bmp);
		pmCanvas.drawBitmap(mPmLayoutBmp, 0, 0, null);
		pmCanvas.drawText("PM 2.5", 43, 35, mPmTitlePaint);
		pmCanvas.drawText("内", 97, 52, mPmMarkPaint);
		pmCanvas.drawText("外", 97, 85, mPmMarkPaint);
		pmCanvas.drawText(String.format("%03d", innerAQI), 45, 70, mPmInnerPaint);
		pmCanvas.drawText(String.format("%03d", outerAQI), 45, 102, mPmOuterPaint);
		return bmp;
	}

}
