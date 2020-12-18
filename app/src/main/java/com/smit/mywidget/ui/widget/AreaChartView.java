package com.smit.mywidget.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class AreaChartView extends View {



    public AreaChartView(Context context) {
        super(context);
    }

    public AreaChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AreaChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
