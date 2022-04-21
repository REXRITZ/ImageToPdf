package com.ritesh.imagetopdf.db;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;

@Entity
public class PdfDetail{
    @PrimaryKey(autoGenerate = true)
    public int id;
    private String fileName;
    private String filePath;
    private final Date dateCreated;
    private final Long fileSize;
    private final boolean isEncrypted;

    public PdfDetail(String fileName, String filePath, Date dateCreated, Long fileSize, boolean isEncrypted) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.dateCreated = dateCreated;
        this.fileSize = fileSize;
        this.isEncrypted = isEncrypted;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }
    public boolean isEncrypted() {
        return isEncrypted;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if(!(obj instanceof PdfDetail)) {
            return false;
        }
        PdfDetail pdfDetail = (PdfDetail) obj;
        return Objects.equals(id, pdfDetail.id)
                && Objects.equals(fileName, pdfDetail.getFileName())
                && Objects.equals(filePath, pdfDetail.getFilePath())
                && Objects.equals(fileSize, pdfDetail.getFileSize())
                && Objects.equals(dateCreated, pdfDetail.getDateCreated())
                && Objects.equals(isEncrypted, pdfDetail.isEncrypted);
    }
}
