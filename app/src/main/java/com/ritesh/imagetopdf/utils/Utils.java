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
        } else if(fileSize < 10e6) {
            name = "kB";
            size /= 1000f;
        } else if(fileSize < 10e9) {
            name = "MB";
            size /= 1000000f;
        } else {
            name = "GB";
            size /= 1000000000f;
        }
        return String.format(Locale.getDefault(),"%.1f %s",size, name);
    }

// --Commented out by Inspection START (12-04-2022 09:56):
//    public static boolean isEncrypted(String path) throws IOException{
//        PDDocument document = PDDocument.load(new File(path),"1");
//        boolean encrypted = document.isEncrypted();
//        document.close();
//        return encrypted;
//    }
// --Commented out by Inspection STOP (12-04-2022 09:56)

// --Commented out by Inspection START (12-04-2022 09:56):
//    public static Bitmap getPdfThumbnail(String path, int width) throws IOException {
//        PdfRenderer pdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(new File(path),ParcelFileDescriptor.MODE_READ_ONLY));
//        Bitmap bitmap;
//        PdfRenderer.Page page = pdfRenderer.openPage(0);
//        Pair<Integer, Integer> dimen = getScaledDimensions(page.getWidth(), page.getHeight(),width * 2, width * 2);
//        bitmap = Bitmap.createBitmap(dimen.first, dimen.second, Bitmap.Config.ARGB_8888);
//        page.render(bitmap,null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//        page.close();
//        pdfRenderer.close();
//        return bitmap;
//    }
// --Commented out by Inspection STOP (12-04-2022 09:56)

    public static Pair<Integer, Integer> getScaledDimensions(int w, int h, int pageWidth, int pageHeight) {
        float ratio = Math.min((float) pageHeight / (float) h, (float) pageWidth / (float) w);
        w = (int) (ratio * w);
        h = (int) (ratio * h);
        return new Pair<>(w,h);
    }
}
