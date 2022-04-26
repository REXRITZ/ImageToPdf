package com.ritesh.imagetopdf.utils;

import android.os.Environment;
import android.util.Pair;

import com.ritesh.imagetopdf.BuildConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    static final String pattern = "yyyy-MM-dd HH:mm";
    public static final String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + "Pdf Files";
    public static final String APPID = BuildConfig.APPLICATION_ID;
    public static final String VERSION = BuildConfig.VERSION_NAME;
    public static final String STORE_LINK = "https://play.google.com/store/apps/details?id=" + APPID;
    public static String getFormattedDate(Date date) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern(pattern);
        return simpleDateFormat.format(date);
    }

    public static String convertFileSize(Long fileSize) {
        String name;
        float size = fileSize.floatValue();
        if(fileSize < 1000) {
            name = "B";
        } else if(fileSize < 1000000) {
            name = "kB";
            size /= 1000f;
        } else if(fileSize < 1000000000) {
            name = "MB";
            size /= 1000000f;
        } else {
            name = "GB";
            size /= 1000000000f;
        }
        return String.format(Locale.getDefault(),"%.1f %s",size, name);
    }

    public static Pair<Integer, Integer> getScaledDimensions(int w, int h, int pageWidth, int pageHeight) {
        float ratio = Math.min((float) pageHeight / (float) h, (float) pageWidth / (float) w);
        w = (int) (ratio * w);
        h = (int) (ratio * h);
        return new Pair<>(w,h);
    }
}
