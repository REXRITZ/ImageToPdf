package com.ritesh.imagetopdf.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class PdfRepository {

    private final PdfDao pdfDao;
    private final LiveData<List<PdfDetail>> allPdfs;

    public PdfRepository(Application application) {
        PdfDatabase db = PdfDatabase.getInstance(application);
        pdfDao = db.pdfDao();
        allPdfs = pdfDao.getAllPdfs();
    }

    public LiveData<List<PdfDetail>> getAllPdfs() {
        return allPdfs;
    }

    public void insert(PdfDetail pdfDetail) {
        PdfDatabase.databaseWriteExecutor.execute(() -> pdfDao.insert(pdfDetail));
    }

    public void update(PdfDetail pdfDetail) {
        PdfDatabase.databaseWriteExecutor.execute(() -> pdfDao.update(pdfDetail));
    }

    public void delete(PdfDetail pdfDetail) {
        PdfDatabase.databaseWriteExecutor.execute(() -> pdfDao.delete(pdfDetail));
    }
}
