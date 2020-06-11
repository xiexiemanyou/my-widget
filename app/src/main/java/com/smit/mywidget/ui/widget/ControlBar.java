package com.smit.mywidget.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.smit.mywidget.R;
import com.smit.mywidget.ui.util.DisplayHelper;

/**
 * @author xiexi
 */
public class ControlBar extends View {

    private final float BIG_ARC_PER_LENGTH = 0.125f;
    private final float LITTLE_ARC_PER_LENGTH = 0.0625f;
    private final float LITTLE_ARC_PER_WIDTH = 0.5f;
    private final float SPACING = DisplayHelper.dpToPx(2);
    private final int MAX_VALUE = 20;
    private final int MIN_VALUE = 2;
    private final int HORIZONTAL = 100;
    private final int VERTICAL = 102;

    private Paint mPaint;
    private Paint mBgPaint;
    private int mBarWidth;
    private int mWidth;
    private int mBarHeight;
    private int mHeight;
    private int mRadius;
    private Path mPath;
    private Path mMaxPath;
    private Path mBgPath;
    private int mValue;
    private float mSlope1;
    private float mSlope2;
    private float mLeftTopMaxPathX;
    private float mLeftTopMaxPathY;
    private float mLeftBottomMaxPathY;
    private float mPercent;
    private int mDirection;

    public ControlBar(Context context) {
        this(context, null);
    }

    public ControlBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        mHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        mBarWidth = mWidth - mHeight;
        mBarHeight = (int) (mHeight * 0.6f);
        mRadius = mHeight / 2;
        if(mWidth >= mHeight){
            mDirection = HORIZONTAL;
            float startY = (mHeight - mBarHeight) / 2.0f;
            mLeftTopMaxPathX = mRadius + mBarWidth * LITTLE_ARC_PER_LENGTH;
            mLeftTopMaxPathY = startY + mBarHeight * (1.0f - LITTLE_ARC_PER_WIDTH) / 2.0f ;
            float rightTopMaxPathX = mRadius + mBarWidth - mBarWidth * BIG_ARC_PER_LENGTH;
            mLeftBottomMaxPathY = mLeftTopMaxPathY + mBarHeight * LITTLE_ARC_PER_WIDTH;
            float rightBottomMaxPathY = startY + mBarHeight;
            float leftRect = mRadius;
            float rightRect = mRadius + mBarWidth;
            float bottomRect = startY + mBarHeight;
            mSlope1 = (startY - mLeftTopMaxPathY) / (rightTopMaxPathX - mLeftTopMaxPathX);
            mSlope2 = (rightBottomMaxPathY - mLeftBottomMaxPathY) / (rightTopMaxPathX - mLeftTopMaxPathX) ;

            settingPath();

            mMaxPath.moveTo(mLeftTopMaxPathX, mLeftTopMaxPathY);
            mMaxPath.lineTo(rightTopMaxPathX, startY);
            mMaxPath.arcTo(rightTopMaxPathX, startY, rightRect, bottomRect, -90, 180, false);
            mMaxPath.lineTo(mLeftTopMaxPathX, mLeftBottomMaxPathY);
            mMaxPath.arcTo(leftRect, mLeftTopMaxPathY, mLeftTopMaxPathX, mLeftBottomMaxPathY, 90, 180, false);
            mMaxPath.close();

            mBgPath.moveTo(mLeftTopMaxPathX - SPACING, mLeftTopMaxPathY - SPACING);
            mBgPath.lineTo(rightTopMaxPathX + SPACING, startY - SPACING);
            mBgPath.arcTo(rightTopMaxPathX + SPACING, startY - SPACING, rightRect + SPACING, bottomRect + SPACING, -90, 180, false);
            mBgPath.lineTo(mLeftTopMaxPathX - SPACING, mLeftBottomMaxPathY + SPACING);
            mBgPath.arcTo(leftRect - SPACING, mLeftTopMaxPathY - SPACING, mLeftTopMaxPathX + SPACING, mLeftBottomMaxPathY + SPACING, 90, 180, false);
            mBgPath.close();

            LinearGradient gradient =new LinearGradient(mLeftTopMaxPathX, mLeftTopMaxPathY, rightTopMaxPathX, rightBottomMaxPathY, Color.BLUE, Color.RED, Shader.TileMode.CLAMP);
            mPaint.setShader(gradient);

        }else {

        }

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBgPaint.setColor(Color.WHITE);
        canvas.drawPath(mBgPath, mBgPaint);
        mBgPaint.setColor(Color.GRAY);
        canvas.drawPath(mMaxPath, mBgPaint);
        canvas.drawPath(mPath, mPaint);
        mBgPaint.setColor(Color.WHITE);
        canvas.drawCircle(mRadius + mPercent * mBarWidth, mRadius, mRadius, mBgPaint);
        canvas.drawCircle(mRadius + mPercent * mBarWidth, mRadius, mRadius  - SPACING, mPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float distance = 0;
                float maxDist = 0;
                switch (mDirection){
                    case HORIZONTAL:
                        distance = event.getX();
                        maxDist = mWidth;
                        break;
                    case VERTICAL:
                        distance = event.getY();
                        maxDist = mHeight;
                        break;
                }
                if(distance <= mRadius){
                    updateView(MIN_VALUE);
                }else if(distance >= maxDist - mRadius){
                    updateView(MAX_VALUE);
                }else {
                    updateView(calculatingValue(distance));
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ControlBar);
        array.recycle();

        mPaint = new Paint();
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mBgPaint = new Paint();
        mBgPaint.setStrokeCap(Paint.Cap.ROUND);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setAntiAlias(true);

        mPath = new Path();
        mMaxPath = new Path();
        mBgPath = new Path();
        mValue = 10;
    }

    private void settingPath(){
        mPath.reset();
        mPercent = (mValue - MIN_VALUE) * 1.0f / (MAX_VALUE - MIN_VALUE);
        mPath.moveTo(mLeftTopMaxPathX, mLeftTopMaxPathY);
        float x1 = mRadius + mPercent * mBarWidth - mRadius * 0.45f;
        float y1 = mSlope1*(x1- mLeftTopMaxPathX) + mLeftTopMaxPathY;
        float x2 = x1 + mRadius * 0.45f;
        float y2 = mSlope2*(x2 - mLeftTopMaxPathX) + mLeftBottomMaxPathY;
        mPath.lineTo(x1 , y1);
        mPath.arcTo(x1, y1, x2, y2, -90, 180, false);
        mPath.lineTo(mLeftTopMaxPathX, mLeftBottomMaxPathY);
        mPath.arcTo(mRadius, mLeftTopMaxPathY, mLeftTopMaxPathX, mLeftBottomMaxPathY, 90, 180, false);
        mPath.close();
    }

    private void updateView(int value){
        mValue = value;
        Log.d("mango", " value = " + mValue);
        settingPath();
        invalidate();
    }

    private int calculatingValue(float distance){
        int total = (mDirection == HORIZONTAL) ? mBarWidth : mBarHeight;
        return (int) (((distance - mRadius) / total) * (MAX_VALUE - MIN_VALUE) + MIN_VALUE);
    }
}
