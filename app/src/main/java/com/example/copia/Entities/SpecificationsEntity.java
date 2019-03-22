package com.example.copia.Entities;

import java.io.Serializable;

public class SpecificationsEntity implements Serializable
{
    private String objectid;
    private String title;
    private String document;
    private String division;
    private String section;
    private String type;
    private String remarkCount;

    public SpecificationsEntity() {}

    public SpecificationsEntity(String objectid, String title, String document, String division, String section, String type, String remarkCount) {
        this.objectid = objectid;
        this.title = title;
        this.document = document;
        this.division = division;
        this.section = section;
        this.type = type;
        this.remarkCount = remarkCount;
    }

    public String getObjectid() {
        return objectid;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemarkCount() {
        return remarkCount;
    }

    public void setRemarkCount(String remarkCount) {
        this.remarkCount = remarkCount;
    }
}
