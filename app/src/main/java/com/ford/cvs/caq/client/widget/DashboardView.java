package com.ford.cvs.caq.client.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.ford.cvs.caq.client.R;
import com.ford.cvs.caq.client.utils.PxUtils;
import com.ford.cvs.caq.client.utils.StringUtil;


public class DashboardView extends View
{

    private DashboardViewAttr dashboardViewattr;
    private int progressStrokeWidth;//进度弧的宽度
    private String speed = "";//速度显示
    private String unit = "";//显示单位
    private int backgroundColor = 0;//背景颜色
    private String mText = ""; //文字内容
    private float startNum;
    private float maxNum;
    private int mTextSize;//文字的大小
    private int mTextColor;//设置文字颜色
    private int mTikeCount;//刻度的个数
    private int startColor;
    private int endColor;
    private int progressColor;
    private int circleColor;

    //画笔
    private Paint paintOutCircle;
    private Paint paintProgressBackground;
    private Paint paintProgress;
    private Paint paintPointer;
    private Paint paintText;
    private Paint paintLeftText;
    private Paint paintNum;
    private RectF rectF1, rectF2;

    private int OFFSET = 30;
    private int START_ARC = 180;
    private int DURING_ARC =180;
    private int mMinCircleRadius; //中心圆点的半径

    private Context mContext;
    private int mWidth, mHight;
    float percent;
    float oldPercent = 0f;
    private ValueAnimator valueAnimator;
    private long animatorDuration;
    TimeInterpolator interpolator = new SpringInterpolator();

    public DashboardView(Context context) {
        this(context, null);
        init(context);
    }

    public DashboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public DashboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dashboardViewattr = new DashboardViewAttr(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        initAttr();
        initPaint();

    }

    private void initPaint() {
        //初始化画笔
        paintOutCircle = new Paint();
        paintOutCircle.setAntiAlias(true);
        paintOutCircle.setStyle(Paint.Style.STROKE);
        paintOutCircle.setStrokeWidth(PxUtils.dpToPx(73, mContext));
        paintOutCircle.setColor(0xfff7f7f7);
        paintOutCircle.setDither(true);
        paintProgressBackground = new Paint();
        paintProgressBackground.setAntiAlias(true);
        paintProgressBackground.setStrokeWidth(progressStrokeWidth);
        paintProgressBackground.setStyle(Paint.Style.STROKE);
        paintProgressBackground.setStrokeCap(Paint.Cap.ROUND);
        paintProgressBackground.setColor(getResources().getColor(R.color.shadow));
        paintProgressBackground.setDither(true);
        paintProgress = new Paint();
        paintProgress.setAntiAlias(true);
        paintProgress.setStrokeWidth(progressStrokeWidth);
        paintProgress.setStyle(Paint.Style.STROKE);
        paintProgress.setStrokeCap(Paint.Cap.ROUND);
        paintProgress.setColor(progressColor);
        paintProgress.setDither(true);
        paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setColor(mTextColor);
        paintText.setStrokeWidth(1);
        paintText.setStyle(Paint.Style.FILL);//实心画笔
        paintText.setDither(true);
        paintLeftText=new Paint();
        paintLeftText.setAntiAlias(true);
        paintLeftText.setColor(mTextColor);
        paintLeftText.setStrokeWidth(1);
        paintLeftText.setStyle(Paint.Style.FILL);//实心画笔
        paintLeftText.setTextSize(PxUtils.spToPx(18, mContext));
        paintLeftText.setDither(true);
        paintText.setColor(0xff63abd9);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintNum = new Paint();
        paintNum.setAntiAlias(true);
        paintNum.setColor(0xffa6faa6);
        paintNum.setStrokeWidth(2);
        paintNum.setStyle(Paint.Style.FILL);
        paintNum.setDither(true);
        paintPointer= new Paint();
        paintPointer.setAntiAlias(true);
        paintPointer.setColor(0xff545454);
        paintPointer.setStyle(Paint.Style.FILL_AND_STROKE);
        paintPointer.setDither(true);

    }


    private void initShader() {
        updateOval();
        if (startColor != 0 && endColor != 0) {
            SweepGradient shader = new SweepGradient(0, 0, new int[]{0xffe52525,0xffe52525,0xff14ec03,0xff14ec03}, null);
            float rotate=90f;
            Matrix gradientMatrix = new Matrix();
            gradientMatrix.preRotate(rotate, 0,0);
            shader.setLocalMatrix(gradientMatrix);
            paintProgress.setShader(shader);
        }
    }

    private void initAttr() {
        mMinCircleRadius = mWidth / 15;
            mTikeCount =100;
        mTextSize = dashboardViewattr.getmTextSize();
        mTextColor = dashboardViewattr.getTextColor();
        mText = dashboardViewattr.getmText();
        progressStrokeWidth = dashboardViewattr.getProgressStrokeWidth();
        unit = dashboardViewattr.getUnit();
        backgroundColor = dashboardViewattr.getBackground();
        startColor = dashboardViewattr.getStartColor();
        endColor = dashboardViewattr.getEndColor();
        startNum = dashboardViewattr.getStartNumber();
        maxNum = dashboardViewattr.getMaxNumber();
        progressColor = dashboardViewattr.getProgressColor();
        circleColor = dashboardViewattr.getCircleColor();
        if (dashboardViewattr.getPadding() == 0) {
            OFFSET= PxUtils.dpToPx(44, mContext);
        } else {
            OFFSET = dashboardViewattr.getPadding();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHight = getHeight();
        initShader();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int realWidth = startMeasure(widthMeasureSpec);
        int realHeight = startMeasure(heightMeasureSpec);

        setMeasuredDimension(realWidth, realHeight);
    }


    private int startMeasure(int msSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(msSpec);
        int size = MeasureSpec.getSize(msSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = PxUtils.dpToPx(200, mContext);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.percent = percent / 100f;
        canvas.translate(mWidth / 2, mHight / 2);//移动坐标原点到中心
        //背景
        drawBackground(canvas);
        //绘制指针和进度弧
        drawPointerAndProgress(canvas, percent);
        //表盘
        drawPanel(canvas);
        //绘制矩形和文字
        drawText(canvas, percent);

    }

    private void drawPointerAndProgress(Canvas canvas, float percent) {
        //绘制粗圆弧
        drawProgress(canvas, percent);

        //绘制指针
        drawerPointer(canvas, percent);
    }

    private void drawPanel(Canvas canvas) {
        //绘制刻度
        drawerNum(canvas);
        //绘制中间小圆和圆环
        drawInPoint(canvas);
    }

    private void drawText(Canvas canvas, float percent) {
        if (TextUtils.isEmpty(unit)) return;
        paintText.setTextSize(mTextSize);
        speed = StringUtil.floatFormat(startNum + (maxNum - startNum) * percent) + unit;
        paintText.setColor(0xff63abd9);
        paintText.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(speed, 0, PxUtils.dpToPx(10, mContext), paintText);
        int numX = -mWidth / 2 + OFFSET + PxUtils.dpToPx(5, mContext) - progressStrokeWidth / 2;
        canvas.drawText("0", numX, PxUtils.dpToPx(23, mContext), paintLeftText);
        numX=mWidth/2-OFFSET;
        paintLeftText.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("100%", numX, PxUtils.dpToPx(23, mContext), paintLeftText);

    }

    private void drawerPointer(Canvas canvas, float percent) {
        mMinCircleRadius = mWidth / 15;
        rectF1 = new RectF(-mMinCircleRadius / 2, -mMinCircleRadius / 2, mMinCircleRadius / 2, mMinCircleRadius / 2);
        canvas.save();
        float angel = DURING_ARC * (percent - 0.5f) - 180;
        canvas.rotate(angel, 0, 0);//指针与外弧边缘持平
        Path pathPointerLeft = new Path();
        pathPointerLeft.moveTo(0, mMinCircleRadius / 2);
        pathPointerLeft.arcTo(rectF1, 270, 90);
        pathPointerLeft.lineTo(0, mHight / 2 - OFFSET - progressStrokeWidth-15);
        pathPointerLeft.lineTo(0, mMinCircleRadius / 2);
        pathPointerLeft.close();

        canvas.drawPath(pathPointerLeft, paintPointer);

        canvas.restore();

    }

    private void drawInPoint(Canvas canvas) {
        Bitmap bitmap= BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.circle_center_icon);
        int radius=bitmap.getWidth()/2;
        canvas.drawBitmap(bitmap,-radius,-radius,null);

    }

    private void drawerNum(Canvas canvas) {
        canvas.save(); //记录画布状态
        canvas.rotate(-(180 - START_ARC + 90), 0, 0);
        int numY = -mHight / 2 + OFFSET + progressStrokeWidth;
        float rAngle = DURING_ARC / ((mTikeCount - 1) * 1.0f); //n根线，只需要n-1个区间
        for (int i = 0; i < mTikeCount; i++) {
            canvas.save(); //记录画布状态
            canvas.rotate(rAngle * i, 0, 0);
            canvas.drawLine(0, numY, 0, numY + PxUtils.dpToPx(4, mContext), paintNum);//画短刻度线
            canvas.restore();
        }
        canvas.restore();
        Bitmap numBitmap= BitmapFactory.decodeResource(getResources(), R.mipmap.tips_num_icon);
        canvas.drawBitmap(numBitmap, numY - PxUtils.dpToPx(40, mContext), numY + PxUtils.dpToPx(10, mContext), null);
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.test), -mWidth / 2, -mHight / 2, null);



    }

    private float getTextViewLength(Paint paint, String text) {
        if (TextUtils.isEmpty(text)) return 0;
        float textLength = paint.measureText(text);
        return textLength;
    }

    private void drawProgress(Canvas canvas, float percent) {
//        canvas.drawArc(rectF2, START_ARC, DURING_ARC, false, paintProgressBackground);
//        if (percent > 1.0f) {
//            percent = 1.0f; //限制进度条在弹性的作用下不会超出
//        }
//        if (!(percent <= 0.0f)) {
//            canvas.drawArc(rectF2, START_ARC, percent * DURING_ARC, false, paintProgress);
//        }
        canvas.drawArc(rectF2, START_ARC,DURING_ARC, false, paintProgress);
    }

    private void updateOval() {
       // OFFSET=100;
        rectF2 = new RectF((-mWidth / 2) + OFFSET + getPaddingLeft(), getPaddingTop() - (mHight / 2) + OFFSET,
                (mWidth / 2) - getPaddingRight() - OFFSET,
                (mWidth / 2) - getPaddingBottom() - OFFSET);
    }

    private void drawBackground(Canvas canvas) {

        //最外阴影
        canvas.drawCircle(0, 0, mWidth/2 - PxUtils.dpToPx(55, mContext), paintOutCircle);
        canvas.save();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        percent = oldPercent;
    }

    /**
     * 设置百分比
     * @param percent
     */
    public void setPercent(int percent) {
        setAnimator(percent);
    }

    private void setAnimator(final float percent) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }

        animatorDuration = (long) Math.abs(percent - oldPercent) * 20;

        valueAnimator = ValueAnimator.ofFloat(oldPercent, percent).setDuration(animatorDuration);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                DashboardView.this.percent = (float) animation.getAnimatedValue();
                invalidate();

            }

        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                oldPercent = percent;

                //防止因为插值器误差产生越界
                if (DashboardView.this.percent < 0.0) {
                    DashboardView.this.percent = 0.0f;
                    invalidate();
                }
                if (DashboardView.this.percent > 100.0) {
                    DashboardView.this.percent = 100.0f;
                    invalidate();
                }
            }
        });
        valueAnimator.start();

    }

    /**
     * 设置文字
     * @param text
     */
    public void setText(String text) {
        mText = text;
        invalidate();
    }


    /**
     * 设置文字大小
     * @param size
     */
    public void setTextSize(int size) {
        mTextSize = size;
        invalidate();
    }

    /**
     * 设置字体颜色
     * @param mTextColor
     */
    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    /**
     * 设置弧的高度
     * @param dp
     */
    public void setProgressStroke(int dp) {

        progressStrokeWidth = PxUtils.dpToPx(dp, mContext);
        paintProgress.setStrokeWidth(progressStrokeWidth);
        paintProgressBackground.setStrokeWidth(progressStrokeWidth);
        invalidate();
    }

    /**
     * 设置单位
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * 设置插值器
     * @param interpolator
     */
    public void setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }

    /**
     * 设置起始颜色
     * @param startColor
     */
    public void setStartColor(int startColor) {
        this.startColor = startColor;
        initShader();
    }

    /**
     * 设置结束颜色
     * @param endColor
     */
    public void setEndColor(int endColor) {
        this.endColor = endColor;
        initShader();
    }

    /**
     * 设置起始值
     * @param startNum
     */
    public void setStartNum(float startNum) {
        this.startNum = startNum;
    }

    /**
     * 设置最大值
     * @param maxNum
     */
    public void setMaxNum(float maxNum) {
        this.maxNum = maxNum;
    }

}
