package com.smit.mywidget.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ProgressBar extends View {

    private int mWidth;
    private int mHeight;
    private int mRecWidth;
    private int mRecHeight;
    private float mStartX;
    private float mStartY;
    private Paint mPaint;
    private Paint mBgPaint;
    private Paint mBorderPaint;
    private int totalCount;
    private int curCount;


    public ProgressBar(Context context) {
        this(context, null);
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(Color.parseColor("#ffffff"));
        mBorderPaint.setStrokeWidth(2f);
        mBorderPaint.setStyle(Paint.Style.STROKE);

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(Color.parseColor("#757575"));

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#757575"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        mWidth = widthSpecSize;
        mHeight = heightSpecSize;
        int width = mWidth, height =  mHeight;


        switch (widthSpecMode) {
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                width = mWidth - getPaddingLeft() - getPaddingRight();
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        switch (heightSpecMode) {
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                height = mHeight - getPaddingTop() - getPaddingBottom();
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        mRecWidth = (int) (width / (totalCount * 1.0f) + 0.5f);
        mRecHeight = height;
        mStartX = (mWidth - width) / 2.0f;
        mStartY = (mHeight - height) / 2.0f;
        LinearGradient gradient =new LinearGradient(mStartX, mStartY, mStartX + width, mStartY + height, Color.BLUE, Color.RED, Shader.TileMode.CLAMP);
        mPaint.setShader(gradient);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float left = 0;
        for(int i = 0; i < totalCount; i++){
            left = mStartX + i * mRecWidth;
            canvas.drawRect(left, mStartY, left + mRecWidth,
                    mStartY + mRecHeight, mBgPaint);

            if(i < curCount){
                canvas.drawRect(left, mStartY, left + mRecWidth,
                        mStartY + mRecHeight, mPaint);
            }

            canvas.drawRect(left, mStartY, left + mRecWidth,
                    mStartY + mRecHeight, mBorderPaint);
        }
    }


    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        invalidate();
    }

    public void setCurCount(int curCount) {
        this.curCount = curCount;
        invalidate();
    }
}
