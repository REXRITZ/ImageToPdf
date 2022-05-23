package com.ritesh.imagetopdf.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Pair;

import com.ritesh.imagetopdf.R;
import com.ritesh.imagetopdf.db.PdfDetail;
import com.ritesh.imagetopdf.viewmodel.PdfViewModel;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission;
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class MakePdfDoc extends AsyncTask<Object, Integer, String> {

    final SaveProgressListener progressListener;
    int pageWidth = 595;
    int pageHeight = 842;
    private String orientation;
    private String auto;
    float quality;
    private String password, fileName;
    private Long fileSize = 0L;
    final PDDocument document;
    final BitmapFactory.Options options;

    public MakePdfDoc(SaveProgressListener progressListener) {
        this.progressListener = progressListener;
        document = new PDDocument();
        options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        File f = new File(Utils.PATH);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public void setNameAndPass(final String pass, final String fileName) {
        this.fileName = fileName;
        this.password = pass;
    }

    public void setOrientation(String orientation, Context context) {
        auto = context.getString(R.string.auto);
        this.orientation = orientation;
        if (orientation.equals(context.getString(R.string.landscape))) {
            pageWidth = 842;
            pageHeight = 595;
        } else if(orientation.equals(context.getString(R.string.portrait))) {
            pageWidth = 595;
            pageHeight = 842;
        }
    }

    public void setQuality(String q, Context context) {
        if(q.equals(context.getString(R.string.high))) {
            quality = 0.8f;
        } else if(q.equals(context.getString(R.string.medium))) {
            quality = 0.6f;
        } else if(q.equals(context.getString(R.string.low))) {
            quality = 0.4f;
        }
    }

    private void convertImageToPdf(Uri path, final ContentResolver contentResolver) throws Exception{
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, path);
        PDImageXObject image = JPEGFactory.createFromImage(document, bitmap,quality);
        int w = image.getWidth();
        int h = image.getHeight();
        Pair<Integer, Integer> scaledDimen = Utils.getScaledDimensions(w, h,pageWidth, pageHeight);
        PDRectangle pageSize;
        float start = 0, top = 0;
        if (this.orientation.equals(auto)) {
            pageSize = new PDRectangle(scaledDimen.first, scaledDimen.second);
        } else {
            start = (pageWidth - scaledDimen.first) / 2f;
            top = (pageHeight - scaledDimen.second) / 2f;
            pageSize = new PDRectangle(pageWidth, pageHeight);
        }
        PDPage page = new PDPage(pageSize);
        document.addPage(page);
        PDPageContentStream contents = new PDPageContentStream(document, page);
        contents.drawImage(image, start, top, scaledDimen.first, scaledDimen.second);
        contents.close();
    }


    @Override
    protected final String doInBackground(Object... params) {
        List<Uri> arrayList = (List<Uri>) params[0];
        ContentResolver contentResolver = (ContentResolver) params[1];
        for (int pageNumber = 0; pageNumber < arrayList.size(); ++pageNumber) {
            publishProgress(pageNumber);
            if (isCancelled()) {
                break;
            }
            try {
                convertImageToPdf(arrayList.get(pageNumber),contentResolver);
            } catch (Exception e) {
                e.printStackTrace();
                return "FAIL";
            }
        }
        File file = new File(Utils.PATH,fileName);
        try {
            if (isCancelled()) {
                return "OK";
            }
            if(password.length() > 0) {
                AccessPermission accessPermission = new AccessPermission();
                accessPermission.setCanPrint(true);
                StandardProtectionPolicy protectionPolicy = new StandardProtectionPolicy(password, password, accessPermission);
                protectionPolicy.setEncryptionKeyLength(128);
                protectionPolicy.setPermissions(accessPermission);
                document.protect(protectionPolicy);
            }
            document.save(file);
            document.close();
            fileSize = file.length();
        } catch (IOException e) {
            e.printStackTrace();
            return "FAIL";
        }
        return "OK";
    }

    @Override
    protected void onPostExecute(String status) {
//        super.onPostExecute(o);
        PdfDetail pdfDetail = new PdfDetail(fileName,
                Utils.PATH + File.separator + fileName,
                new Date(),
                fileSize, password.length() > 0);
        PdfViewModel.insert(pdfDetail);
        progressListener.onDone(status);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
//        super.onProgressUpdate(values);
        progressListener.onProgressUpdate(values[0] + 1);
    }

    public interface SaveProgressListener {
        void onProgressUpdate(int progress);
        void onDone(String status);
    }
}
