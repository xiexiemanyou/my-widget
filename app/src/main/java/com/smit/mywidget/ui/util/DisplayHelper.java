package com.smit.mywidget.ui.util;

import android.content.res.Resources;

/**
 * @author xiexi
 */
public class DisplayHelper {

    public static final float DENSITY = Resources.getSystem()
            .getDisplayMetrics().density;

    public static int dpToPx(int dpValue) {
        return (int) (dpValue * DENSITY + 0.5f);
    }
}
