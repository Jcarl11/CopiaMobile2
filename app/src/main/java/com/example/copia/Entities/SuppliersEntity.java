package com.example.copia.Entities;

import java.io.Serializable;

public class SuppliersEntity implements Serializable {
    String objectId;
    String representative;
    String position;
    String company;
    String brand;
    String discipline;
    String remarkCount;

    public SuppliersEntity() {}

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public String getRemarkCount() {
        return remarkCount;
    }

    public void setRemarkCount(String remarkCount) {
        this.remarkCount = remarkCount;
    }
}
