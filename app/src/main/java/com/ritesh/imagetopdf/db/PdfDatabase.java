package com.ritesh.imagetopdf.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ritesh.imagetopdf.utils.MyConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {PdfDetail.class}, version = 1, exportSchema = false)
@TypeConverters({MyConverter.class})
public abstract class PdfDatabase extends RoomDatabase {

    private static volatile PdfDatabase INSTANCE = null;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static PdfDatabase getInstance(final Context context) {
        if(INSTANCE == null) {
            synchronized (PdfDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PdfDatabase.class,
                            "PdfDatabase").build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract PdfDao pdfDao();
}
