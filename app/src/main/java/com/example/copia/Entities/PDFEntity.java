package com.example.copia.Entities;

import java.util.ArrayList;

public class PDFEntity
{
    private String objectId;
    private String filename;
    private byte[] file;
    private String createdAt;

    public PDFEntity() {}

    public PDFEntity(String objectId, String filename, byte[] file, String createdAt) {
        this.objectId = objectId;
        this.filename = filename;
        this.file = file;
        this.createdAt = createdAt;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
