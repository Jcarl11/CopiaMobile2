package com.example.copia.Entities;

import java.io.Serializable;

public class ContractorsEntity implements Serializable {
    private String objectId;
    private String representative;
    private String position;
    private String company;
    private String specialization;
    private String classification;
    private String industry;
    private String remarkCount;

    public ContractorsEntity() {}

    public ContractorsEntity(String objectId, String representative, String position, String company, String specialization, String classification, String industry, String remarkCount) {
        this.objectId = objectId;
        this.representative = representative;
        this.position = position;
        this.company = company;
        this.specialization = specialization;
        this.classification = classification;
        this.industry = industry;
        this.remarkCount = remarkCount;
    }

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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getRemarkCount() {
        return remarkCount;
    }

    public void setRemarkCount(String remarkCount) {
        this.remarkCount = remarkCount;
    }
}
