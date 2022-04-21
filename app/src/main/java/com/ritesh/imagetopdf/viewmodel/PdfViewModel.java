package com.ritesh.imagetopdf.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ritesh.imagetopdf.db.PdfDetail;
import com.ritesh.imagetopdf.db.PdfRepository;

import java.io.File;
import java.util.List;

public class PdfViewModel extends AndroidViewModel {

    private static PdfRepository pdfRepository;
    private final LiveData<List<PdfDetail>> allPdfs;
    public PdfViewModel(@NonNull Application application) {
        super(application);
        pdfRepository = new PdfRepository(application);
        allPdfs = pdfRepository.getAllPdfs();
    }


    public LiveData<List<PdfDetail>> getAllPdfs() {
        return allPdfs;
    }

    public static void insert(PdfDetail pdfDetail) {
        pdfRepository.insert(pdfDetail);
    }

    public static void update(PdfDetail pdfDetail) {
        pdfRepository.update(pdfDetail);
    }

    public static void delete(PdfDetail pdfDetail) {
        File file = new File(pdfDetail.getFilePath());
        file.delete();
        pdfRepository.delete(pdfDetail);
    }
}
