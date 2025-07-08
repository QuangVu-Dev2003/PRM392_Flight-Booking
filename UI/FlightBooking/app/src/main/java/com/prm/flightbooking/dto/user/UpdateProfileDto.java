package com.prm.flightbooking.dto.user;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileDto {
    @SerializedName("FullName")
    private String fullName;

    @SerializedName("Phone")
    private String phone;

    @SerializedName("DateOfBirth")
    private String dateOfBirth; // định dạng "yyyy-MM-dd"

    @SerializedName("Gender")
    private String gender;

    public UpdateProfileDto() {
    }

    public UpdateProfileDto(String fullName, String phone, String dateOfBirth, String gender) {
        this.fullName = fullName;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
