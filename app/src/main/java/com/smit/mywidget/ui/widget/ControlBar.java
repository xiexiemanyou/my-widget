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
    private final int MAX_VALUE = 50;
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
    private float mLArcFirstPathX;
    private float mLArcFirstPathY;
    private float mLArcSecPathX;
    private float mLArcSecPathY;
    private float mBArcFirstPathX;
    private float mBArcFirstPathY;
    private float mBArcSecPathX;
    private float mBArcSecPathY;
    private float mLArcLength;
    private float mBArcLength;
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

        if(mWidth >= mHeight){
            mBarWidth = mWidth - mHeight;
            mBarHeight = (int) (mHeight * 0.6f);
            mRadius = mHeight / 2;
            mDirection = HORIZONTAL;
            float startY = (mHeight - mBarHeight) / 2.0f;
            mLArcLength = mBarWidth * LITTLE_ARC_PER_LENGTH;
            mLArcFirstPathX = mRadius + mLArcLength;
            mLArcFirstPathY = startY + mBarHeight * (1.0f - LITTLE_ARC_PER_WIDTH) / 2.0f ;
            mBArcLength = mBarWidth * BIG_ARC_PER_LENGTH;
            mBArcFirstPathX = mRadius + mBarWidth - mBArcLength;
            mBArcFirstPathY = startY;
            mBArcSecPathX = mBArcFirstPathX;
            mBArcSecPathY = startY + mBarHeight;
            mLArcSecPathX = mLArcFirstPathX;
            mLArcSecPathY = mLArcFirstPathY + mBarHeight * LITTLE_ARC_PER_WIDTH;
            mSlope1 = (startY - mLArcFirstPathY) / (mBArcFirstPathX - mLArcFirstPathX);
            mSlope2 = (mBArcSecPathY - mLArcSecPathY) / (mBArcFirstPathX - mLArcFirstPathX) ;

            settingPath();

            mMaxPath.moveTo(mLArcFirstPathX, mLArcFirstPathY);
            mMaxPath.lineTo(mBArcFirstPathX, mBArcFirstPathY);
            mMaxPath.arcTo(mBArcFirstPathX, mBArcFirstPathY, mBArcFirstPathX + mBArcLength, mBArcSecPathY, -90, 180, false);
            mMaxPath.lineTo(mLArcFirstPathX, mLArcSecPathY);
            mMaxPath.arcTo(mLArcFirstPathX - mLArcLength, mLArcFirstPathY, mLArcFirstPathX, mLArcSecPathY, 90, 180, false);
            mMaxPath.close();

            mBgPath.moveTo(mLArcFirstPathX - SPACING, mLArcFirstPathY - SPACING);
            mBgPath.lineTo(mBArcFirstPathX + SPACING, mBArcFirstPathY - SPACING);
            mBgPath.arcTo(mBArcFirstPathX + SPACING, mBArcFirstPathY - SPACING, mBArcFirstPathX + mBArcLength + SPACING, mBArcSecPathY + SPACING, -90, 180, false);
            mBgPath.lineTo(mLArcFirstPathX - SPACING, mLArcSecPathY + SPACING);
            mBgPath.arcTo(mLArcFirstPathX - mLArcLength - SPACING, mLArcFirstPathY - SPACING, mLArcFirstPathX + SPACING, mLArcSecPathY + SPACING, 90, 180, false);
            mBgPath.close();

            LinearGradient gradient =new LinearGradient(mLArcFirstPathX, mLArcFirstPathY, mBArcFirstPathX, mBArcSecPathY, Color.BLUE, Color.RED, Shader.TileMode.CLAMP);
            mPaint.setShader(gradient);

        }else {
            mBarWidth = (int) (mWidth * 0.6f);;
            mBarHeight = mHeight - mWidth;
            mRadius = mWidth / 2;
            mDirection = VERTICAL;
            float startX = (mWidth - mBarWidth) / 2.0f;
            mLArcFirstPathX = startX + mBarWidth * (1.0f - LITTLE_ARC_PER_WIDTH) / 2.0f;
            mLArcFirstPathY = mRadius + mBarHeight * (1.0f - LITTLE_ARC_PER_LENGTH);
            float rightTopMaxPathX = startX;
            float mRightTopMaxPathY = mRadius + mBarHeight * BIG_ARC_PER_LENGTH;
            mLArcSecPathY = mLArcFirstPathY;
            float rightBottomMaxPathY = mRadius;
            float leftRect = mRadius;
            float rightRect = mRadius + mBarWidth;
            float bottomRect = startX + mBarHeight;
            mSlope1 = (mRightTopMaxPathY - mLArcFirstPathY) / (rightTopMaxPathX - mLArcFirstPathX);
            mSlope2 = (rightBottomMaxPathY - mLArcSecPathY) / (rightTopMaxPathX - mLArcFirstPathX) ;

            settingPath();

            mMaxPath.moveTo(mLArcFirstPathX, mLArcFirstPathY);
            mMaxPath.lineTo(rightTopMaxPathX, startX);
            mMaxPath.arcTo(rightTopMaxPathX, startX, rightRect, bottomRect, -90, 180, false);
            mMaxPath.lineTo(mLArcFirstPathX, mLArcSecPathY);
            mMaxPath.arcTo(leftRect, mLArcFirstPathY, mLArcFirstPathX, mLArcSecPathY, 90, 180, false);
            mMaxPath.close();

            mBgPath.moveTo(mLArcFirstPathX - SPACING, mLArcFirstPathY - SPACING);
            mBgPath.lineTo(rightTopMaxPathX + SPACING, startX - SPACING);
            mBgPath.arcTo(rightTopMaxPathX + SPACING, startX - SPACING, rightRect + SPACING, bottomRect + SPACING, -90, 180, false);
            mBgPath.lineTo(mLArcFirstPathX - SPACING, mLArcSecPathY + SPACING);
            mBgPath.arcTo(leftRect - SPACING, mLArcFirstPathY - SPACING, mLArcFirstPathX + SPACING, mLArcSecPathY + SPACING, 90, 180, false);
            mBgPath.close();

            LinearGradient gradient =new LinearGradient(mLArcFirstPathX, mLArcFirstPathY, rightTopMaxPathX, rightBottomMaxPathY, Color.BLUE, Color.RED, Shader.TileMode.CLAMP);
            mPaint.setShader(gradient);
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
        mPath.moveTo(mLArcFirstPathX, mLArcFirstPathY);
        float x1 = mRadius + mPercent * mBarWidth - mRadius * 0.45f;
        float y1 = mSlope1*(x1- mLArcFirstPathX) + mLArcFirstPathY;
        float x2 = x1 + mRadius * 0.45f;
        float y2 = mSlope2*(x2 - mLArcFirstPathX) + mLArcSecPathY;
        mPath.lineTo(x1 , y1);
        mPath.arcTo(x1, y1, x2, y2, -90, 180, false);
        mPath.lineTo(mLArcSecPathX, mLArcSecPathY);
        mPath.arcTo(mLArcFirstPathX - mLArcLength, mLArcFirstPathY, mLArcFirstPathX, mLArcSecPathY, 90, 180, false);
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
