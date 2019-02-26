package com.example.copia.Entities;

import java.io.Serializable;

public class UserEntity implements Serializable {
    private String objectId;
    private boolean verified;
    private String fullname;
    private String email;
    private String username;
    private String position;

    public UserEntity() {}

    public UserEntity(String objectId, boolean verified, String fullname, String email, String username, String position) {
        this.objectId = objectId;
        this.verified = verified;
        this.fullname = fullname;
        this.email = email;
        this.username = username;
        this.position = position;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
