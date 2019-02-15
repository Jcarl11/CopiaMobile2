package com.example.copia.Entities;

import java.io.File;

public class ImagesEntity
{
    private String objectId;
    private String imageName;
    private String size;
    private byte[] image;

    public ImagesEntity(String objectId, String imageName, String size, byte[] image) {
        this.objectId = objectId;
        this.imageName = imageName;
        this.size = size;
        this.image = image;
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
}
