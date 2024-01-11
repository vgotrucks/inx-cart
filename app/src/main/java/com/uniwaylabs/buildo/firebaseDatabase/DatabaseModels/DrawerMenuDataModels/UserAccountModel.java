package com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.DrawerMenuDataModels;

import java.io.Serializable;
import java.util.Date;

public class UserAccountModel implements Serializable {
    private String username;
    private String phone;
    private String profileImgUrl;
    private String emailId;
    private String givenName;
    private String familyName;
    private Date birthdate;
    private double positionLat;
    private double PositionLng;

    public UserAccountModel() {
    }

    public UserAccountModel(String username, String phone, String profileImgUrl, String emailId, String givenName, String familyName, Date birthdate, double positionLat, double positionLng) {
        this.username = username;
        this.phone = phone;
        this.profileImgUrl = profileImgUrl;
        this.emailId = emailId;
        this.givenName = givenName;
        this.familyName = familyName;
        this.birthdate = birthdate;
        this.positionLat = positionLat;
        PositionLng = positionLng;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public double getPositionLat() {
        return positionLat;
    }

    public void setPositionLat(double positionLat) {
        this.positionLat = positionLat;
    }

    public double getPositionLng() {
        return PositionLng;
    }

    public void setPositionLng(double positionLng) {
        PositionLng = positionLng;
    }
}
