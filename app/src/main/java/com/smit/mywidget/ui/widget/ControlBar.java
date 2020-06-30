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
    private float mHorLArcFirstPathX;
    private float mHorLArcFirstPathY;
    private float mHorLArcSecPathX;
    private float mHorLArcSecPathY;
    private float mHorBArcFirstPathX;
    private float mHorBArcFirstPathY;
    private float mHorBArcSecPathX;
    private float mHorBArcSecPathY;
    private float mLArcLength;
    private float mBArcLength;

    private float mLArcFirstPathX;
    private float mLArcFirstPathY;
    private float mLArcSecPathX;
    private float mLArcSecPathY;
    private float mBArcFirstPathX;
    private float mBArcFirstPathY;
    private float mBArcSecPathX;
    private float mBArcSecPathY;

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
        int longSide, shortSide;
        LinearGradient gradient;
        if(mWidth >= mHeight){
            longSide = mWidth;
            shortSide = mHeight;
            mDirection = HORIZONTAL;
        }else {
            longSide = mHeight;
            shortSide = mWidth;
            mDirection = VERTICAL;
        }

        mBarWidth = longSide - shortSide;
        mBarHeight = (int) (shortSide * 0.6f);
        mRadius = shortSide / 2;
        float startY = (shortSide - mBarHeight) / 2.0f;
        mLArcLength = mBarWidth * LITTLE_ARC_PER_LENGTH;
        mHorLArcFirstPathX = mRadius + mLArcLength;
        mHorLArcFirstPathY = startY + mBarHeight * (1.0f - LITTLE_ARC_PER_WIDTH) / 2.0f ;
        mLArcFirstPathX = mHorLArcFirstPathY;
        mLArcFirstPathY = -mHorLArcFirstPathX + longSide;
        mBArcLength = mBarWidth * BIG_ARC_PER_LENGTH;
        mHorBArcFirstPathX = mRadius + mBarWidth - mBArcLength;
        mHorBArcFirstPathY = startY;
        mBArcFirstPathX = mHorBArcFirstPathY;
        mBArcFirstPathY = -mHorBArcFirstPathX + longSide;
        mHorBArcSecPathX = mHorBArcFirstPathX;
        mHorBArcSecPathY = startY + mBarHeight;
        mBArcSecPathX = mHorBArcSecPathY;
        mBArcSecPathY = -mHorBArcSecPathX + longSide;
        mHorLArcSecPathX = mHorLArcFirstPathX;
        mHorLArcSecPathY = mHorLArcFirstPathY + mBarHeight * LITTLE_ARC_PER_WIDTH;
        mLArcSecPathX = mHorLArcSecPathY;
        mLArcSecPathY = -mHorLArcSecPathX + longSide;
        mSlope1 = (startY - mHorLArcFirstPathY) / (mHorBArcFirstPathX - mHorLArcFirstPathX);
        mSlope2 = (mHorBArcSecPathY - mHorLArcSecPathY) / (mHorBArcFirstPathX - mHorLArcFirstPathX) ;

        settingPath();

        if(mDirection == HORIZONTAL){
            mMaxPath.moveTo(mHorLArcFirstPathX, mHorLArcFirstPathY);
            mMaxPath.lineTo(mHorBArcFirstPathX, mHorBArcFirstPathY);
            mMaxPath.arcTo(mHorBArcFirstPathX, mHorBArcFirstPathY, mHorBArcFirstPathX + mBArcLength, mHorBArcSecPathY, -90, 180, false);
            mMaxPath.lineTo(mHorLArcFirstPathX, mHorLArcSecPathY);
            mMaxPath.arcTo(mHorLArcFirstPathX - mLArcLength, mHorLArcFirstPathY, mHorLArcFirstPathX, mHorLArcSecPathY, 90, 180, false);
            mMaxPath.close();

            mBgPath.moveTo(mHorLArcFirstPathX - SPACING, mHorLArcFirstPathY - SPACING);
            mBgPath.lineTo(mHorBArcFirstPathX + SPACING, mHorBArcFirstPathY - SPACING);
            mBgPath.arcTo(mHorBArcFirstPathX + SPACING, mHorBArcFirstPathY - SPACING, mHorBArcFirstPathX + mBArcLength + SPACING, mHorBArcSecPathY + SPACING, -90, 180, false);
            mBgPath.lineTo(mHorLArcFirstPathX - SPACING, mHorLArcSecPathY + SPACING);
            mBgPath.arcTo(mHorLArcFirstPathX - mLArcLength - SPACING, mHorLArcFirstPathY - SPACING, mHorLArcFirstPathX + SPACING, mHorLArcSecPathY + SPACING, 90, 180, false);
            mBgPath.close();
            gradient =new LinearGradient(mHorLArcFirstPathX, mHorLArcFirstPathY, mHorBArcFirstPathX, mHorBArcSecPathY, Color.BLUE, Color.RED, Shader.TileMode.CLAMP);
        }else {
            mMaxPath.moveTo(mLArcFirstPathX, mLArcFirstPathY);
            mMaxPath.lineTo(mBArcFirstPathX, mBArcFirstPathY);
            mMaxPath.arcTo(mBArcFirstPathX, mBArcFirstPathY - mBArcLength, mBArcSecPathX, mBArcSecPathY, 180, 180, false);
            mMaxPath.lineTo(mLArcSecPathX, mLArcSecPathY);
            mMaxPath.arcTo(mLArcFirstPathX, mLArcFirstPathY, mLArcSecPathX, mLArcSecPathY + mLArcLength, 0, 180, false);
            mMaxPath.close();

            mBgPath.moveTo(mLArcFirstPathX - SPACING, mLArcFirstPathY + SPACING);
            mBgPath.lineTo(mBArcFirstPathX - SPACING, mBArcFirstPathY - SPACING);
            mBgPath.arcTo(mBArcFirstPathX - SPACING, mBArcFirstPathY - mBArcLength - SPACING, mBArcSecPathX + SPACING, mBArcSecPathY - SPACING, 180, 180, false);
            mBgPath.lineTo(mLArcSecPathX + SPACING, mLArcSecPathY + SPACING);
            mBgPath.arcTo(mLArcFirstPathX - SPACING, mLArcFirstPathY + SPACING, mLArcSecPathX + SPACING, mLArcSecPathY + mLArcLength + SPACING, 0, 180, false);
            mBgPath.close();
            gradient =new LinearGradient(mLArcFirstPathX, mLArcFirstPathY, mBArcFirstPathX, mBArcSecPathY, Color.BLUE, Color.RED, Shader.TileMode.CLAMP);
        }

        mPaint.setShader(gradient);
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
        if(mDirection == HORIZONTAL){
            canvas.drawCircle(mRadius + mPercent * mBarWidth, mRadius, mRadius, mBgPaint);
            canvas.drawCircle(mRadius + mPercent * mBarWidth, mRadius, mRadius  - SPACING, mPaint);
        }else {
            canvas.drawCircle(mRadius, mHeight - (mRadius + mPercent * mBarWidth), mRadius, mBgPaint);
            canvas.drawCircle(mRadius, mHeight - (mRadius + mPercent * mBarWidth), mRadius  - SPACING, mPaint);
        }

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
                        distance = mHeight - event.getY();
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
        float x1 = mRadius + mPercent * mBarWidth - mRadius * 0.45f;
        float y1 = mSlope1*(x1- mHorLArcFirstPathX) + mHorLArcFirstPathY;
        float x2 = x1 + mRadius * 0.45f;
        float y2 = mSlope2*(x2 - mHorLArcFirstPathX) + mHorLArcSecPathY;
        if(mDirection == HORIZONTAL){
            mPath.moveTo(mHorLArcFirstPathX, mHorLArcFirstPathY);
            mPath.lineTo(x1 , y1);
            mPath.arcTo(x1, y1, x2, y2, -90, 180, false);
            mPath.lineTo(mHorLArcSecPathX, mHorLArcSecPathY);
            mPath.arcTo(mHorLArcFirstPathX - mLArcLength, mHorLArcFirstPathY, mHorLArcFirstPathX, mHorLArcSecPathY, 90, 180, false);
        }else {
            mPath.moveTo(mLArcFirstPathX, mLArcFirstPathY);
            mPath.lineTo(y1 , -x1 + mHeight);
            mPath.arcTo(y1 , -x2 + mHeight, y2, -x1 + mHeight, 180, 180, false);
            mPath.lineTo(mLArcSecPathX, mLArcSecPathY);
            mPath.arcTo(mLArcFirstPathX, mLArcFirstPathY, mLArcSecPathX, mLArcSecPathY + mLArcLength, 0, 180, false);
        }

        mPath.close();
    }

    private void updateView(int value){
        mValue = value;
        settingPath();
        invalidate();
    }

    private int calculatingValue(float distance){
        return (int) (((distance - mRadius) / mBarWidth) * (MAX_VALUE - MIN_VALUE) + MIN_VALUE);
    }
}
