package com.example.copia.Entities;


public class ClientEntity
{
    String objectId;
    String representative;
    String position;
    String company;
    String industry;
    String type;

    public ClientEntity() {}

    public ClientEntity(String objectId, String representative, String position, String company, String industry, String type) {
        this.objectId = objectId;
        this.representative = representative;
        this.position = position;
        this.company = company;
        this.industry = industry;
        this.type = type;
    }

    public String getObjectId() {return objectId;}

    public void setObjectId(String objectId) {this.objectId = objectId;}

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getRepresentative() {return representative;}

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

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
