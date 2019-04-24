package com.ford.cvs.caq.client.layout;

import android.content.Context;
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
import android.support.v4.graphics.ColorUtils;

import com.ford.cvs.caq.client.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;


/**
 * Created by leon on 2017/3/1.
 */

public class SdlAQILayoutCrystal extends SdlAQILayout
{
	// SINGLETON
	private static SdlAQILayoutCrystal mInstance = null;
	public static SdlAQILayoutCrystal getInstance(Context ctx)
	{
		if (mInstance == null)
		{
			mInstance = new SdlAQILayoutCrystal();
			mInstance.init(ctx);
		}
		return mInstance;
	}

	protected SdlAQILayoutCrystal()
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
		mTempPaint.setColor(Color.argb(255, 160, 160, 160));
		mTempPaint.setTextSize(38f);
		mTempPaint.setAntiAlias(true);
		mTempPaint.setTypeface(mHeitiFont);

		mWeatherPaint = new Paint();
		mWeatherPaint.setTextAlign(Paint.Align.LEFT);
		mWeatherPaint.setColor(Color.GRAY);
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
		mPmOuterTitlePaint.setTextSize(20f);
		mPmOuterTitlePaint.setFakeBoldText(true);
		mPmOuterTitlePaint.setAntiAlias(true);
		mPmOuterTitlePaint.setTypeface(mHeitiFont);

		mPmInnerPaint = new Paint();
		mPmInnerPaint.setTextAlign(Paint.Align.CENTER);
		mPmInnerPaint.setColor(Color.WHITE);
		mPmInnerPaint.setTextSize(60f);
		mPmInnerPaint.setAntiAlias(true);
		mPmInnerPaint.setTypeface(mHeitiFont);

		mPmOuterPaint = new Paint();
		mPmOuterPaint.setTextAlign(Paint.Align.CENTER);
		mPmOuterPaint.setTextSize(60f);
		mPmOuterPaint.setAntiAlias(true);
		mPmOuterPaint.setTypeface(mHeitiFont);

		mInnerFontMetrics = mPmInnerPaint.getFontMetricsInt();
		mOuterFontMetrics = mPmOuterPaint.getFontMetricsInt();

	}


	protected InputStream getAQILayoutImage(int innerAQI, int outerAQI, WeatherInfo wi)
	{
		Bitmap mainLayoutBmp = Bitmap.createBitmap(LAYOUT_WIDTH, LAYOUT_HEIGHT, Bitmap.Config.ARGB_8888);
		int innerAQIColor = getAQIColor(innerAQI);
		int outerAQIColor = getAQIColor(outerAQI);

		mPmOuterPaint.setColor(outerAQIColor);
		mPmOuterTitlePaint.setColor(outerAQIColor);

		// Paint for gradient effect
		Paint paint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);

		Canvas layoutCanvas = new Canvas(mainLayoutBmp);
		layoutCanvas.drawARGB(255, 255, 255, 255);

		// Draw outer sector
		paint.setColor(ColorUtils.blendARGB(innerAQIColor, Color.WHITE, 0.8f));
		RectF innerArc1 = new RectF(LAYOUT_WIDTH/2 - 100, -40, LAYOUT_WIDTH/2 + 100, LAYOUT_HEIGHT + 40);
		layoutCanvas.drawArc(innerArc1, 90 , -180, false, paint);

		// Draw inner sector
		paint.setColor(ColorUtils.blendARGB(innerAQIColor, Color.WHITE, 0.5f));
		RectF innerArc2 = new RectF(LAYOUT_WIDTH/2 - 90, 0, LAYOUT_WIDTH/2 + 90, LAYOUT_HEIGHT);
		layoutCanvas.drawArc(innerArc2, 90 , -180, false, paint);

		// Draw gradient rectangle
		LinearGradient linearGradient = new LinearGradient(0, 0, LAYOUT_WIDTH/2, 0,
														   ColorUtils.blendARGB(outerAQIColor, Color.WHITE, 0.5f),
														   ColorUtils.blendARGB(innerAQIColor, Color.WHITE, 0.5f),
														   Shader.TileMode.CLAMP);
		paint.setShader(linearGradient);
		layoutCanvas.drawRect(0, 0, LAYOUT_WIDTH/2, LAYOUT_HEIGHT, paint);

		// Draw inner AQI circle
		paint.setColor(innerAQIColor);
		paint.setShader(null);
		paint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.INNER));
		RectF innerEllipse = new RectF(LAYOUT_WIDTH/2 - 10 - 90, 35, LAYOUT_WIDTH/2 - 10 + 90 , LAYOUT_HEIGHT - 35);
		layoutCanvas.drawOval(innerEllipse, paint);

		// Draw inner AQI indicator
		layoutCanvas.drawBitmap(mInnerIndicatorBmp,
								LAYOUT_WIDTH/2 + 100 - 30 - mInnerIndicatorBmp.getWidth(),
								LAYOUT_HEIGHT/2 - mInnerIndicatorBmp.getHeight() / 2, null);

		// Draw outer AQI indicator
		layoutCanvas.drawBitmap(mOuterIndicatorBmp,
								LAYOUT_WIDTH/2 + 100 + 30 - mOuterIndicatorBmp.getWidth(),
								LAYOUT_HEIGHT/2 - mOuterIndicatorBmp.getHeight() / 2, null);

		// Draw AQI title in circle
		layoutCanvas.drawText("内部PM2.5", 325, 90, mPmInnerTitlePaint);
		layoutCanvas.drawText("外部PM2.5", 520, 90, mPmOuterTitlePaint);


		// Draw AQI value with horizontal align
		Rect innerAQITextRect = new Rect(LAYOUT_WIDTH/2 - 60, 100, LAYOUT_WIDTH/2 - 60 + 100, 165);
		layoutCanvas.drawText(String.valueOf(innerAQI), innerAQITextRect.centerX(),
							  (innerAQITextRect.bottom + innerAQITextRect.top - mInnerFontMetrics.bottom - mInnerFontMetrics.top) / 2,
							  mPmInnerPaint);

		innerAQITextRect.set(LAYOUT_WIDTH/2 + 135, 100, LAYOUT_WIDTH/2 + 135 + 100, 165);
		layoutCanvas.drawText(String.valueOf(outerAQI), innerAQITextRect.centerX(),
							  (innerAQITextRect.bottom + innerAQITextRect.top - mOuterFontMetrics.bottom - mOuterFontMetrics.top) / 2,
							  mPmOuterPaint);

		// Draw weather logo
		layoutCanvas.drawBitmap(getConditionBmp(mCtx, wi.condition), 645, 30, null);

		// Draw temperature & weather info
		layoutCanvas.drawText(wi.city, 680, 55, mWeatherPaint);
		layoutCanvas.drawText(wi.temperature + "°", 675, 100, mTempPaint);

		layoutCanvas.drawText(wi.windDir + wi.windLevel + "级", 675, 125, mWeatherPaint);
		layoutCanvas.drawText(wi.humidity + "%RH", 675, 150, mWeatherPaint);
		layoutCanvas.drawText(wi.pressure + "hPa", 675, 175, mWeatherPaint);


		// Draw Immerse effect
		Bitmap outBmp = Bitmap.createBitmap(LAYOUT_WIDTH, LAYOUT_HEIGHT, Bitmap.Config.ARGB_8888);
		Canvas outCanvas = new Canvas(outBmp);
		paint = new Paint();
		paint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
		outCanvas.drawBitmap(roundCornerImg(mainLayoutBmp, 50), 0, 0, paint);

		//show img on phone
//		try
//		{
//			final String filePathName = "/sdcard/test.png";
//			File myCaptureFile = new File(filePathName);
//
//			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
//			outBmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
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
		outBmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return new ByteArrayInputStream(baos.toByteArray());
	}

}
