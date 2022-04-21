package com.ritesh.imagetopdf.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PdfDao {

    @Insert
    void insert(PdfDetail pdfDetail);

    @Update
    void update(PdfDetail pdfDetail);

    @Delete
    void delete(PdfDetail pdfDetail);

    @Query("SELECT * FROM PdfDetail ORDER BY id DESC")
    LiveData<List<PdfDetail>> getAllPdfs();

}
