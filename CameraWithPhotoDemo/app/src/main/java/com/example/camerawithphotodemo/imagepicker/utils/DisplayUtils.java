package com.example.camerawithphotodemo.imagepicker.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

public class DisplayUtils {

    public static ScreenSize getScreenSize(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new ScreenSize(metrics.widthPixels, metrics.heightPixels);
    }

}
