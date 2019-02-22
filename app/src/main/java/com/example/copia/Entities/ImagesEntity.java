package com.example.copia.Entities;

import java.io.File;
import java.io.Serializable;

public class ImagesEntity implements Serializable
{
    private String objectId;
    private String imageName;
    private String size;
    private byte[] image;
    private String url;

    public ImagesEntity(String objectId, String imageName, String size, byte[] image, String url) {
        this.objectId = objectId;
        this.imageName = imageName;
        this.size = size;
        this.image = image;
        this.url = url;
    }

    public ImagesEntity() {}

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
