package com.example.copia.Entities;

import java.io.Serializable;
import java.util.ArrayList;

public class PDFEntity implements Serializable
{
    private String objectId;
    private String filename;
    private String createdAt;
    private String url;
    private String size;
    public PDFEntity() {}

    public PDFEntity(String objectId, String filename, String createdAt, String url, String size) {
        this.objectId = objectId;
        this.filename = filename;
        this.createdAt = createdAt;
        this.url = url;
        this.size = size;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
