package com.example.copia.Entities;

public class ClientEntity
{
    private String representative;
    private String position;
    private String company;
    private String industry;
    private String type;

    public ClientEntity() {}

    public ClientEntity(String representative, String position, String company, String industry, String type) {
        this.representative = representative;
        this.position = position;
        this.company = company;
        this.industry = industry;
        this.type = type;
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
