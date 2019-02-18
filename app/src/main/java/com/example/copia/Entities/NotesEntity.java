package com.example.copia.Entities;

public class NotesEntity {
    private String objectId;
    private String remark;
    private String createdAt;

    public NotesEntity() {}

    public NotesEntity(String objectId, String remark, String createdAt) {
        this.objectId = objectId;
        this.remark = remark;
        this.createdAt = createdAt;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
