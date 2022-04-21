package com.ritesh.imagetopdf.utils;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import java.util.Date;

public class MyConverter {

    @NonNull
    @TypeConverter
    public static Date timeStampToDate(Long timestamp) {
        return new Date(timestamp);
    }

    @NonNull
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date.getTime();
    }

}
