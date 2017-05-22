package com.notenow.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class UtilTypeface {

    private static final String loc = "fonts/orkney-light.otf";

    public static void setCustomTypeface(Context context, TextView... textView) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), loc);
        for (TextView tv : textView) {
            tv.setTypeface(typeface);
        }
    }
}