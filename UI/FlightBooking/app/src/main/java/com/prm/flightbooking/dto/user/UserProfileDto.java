package com.prm.flightbooking.dto.user;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class UserProfileDto {
    @SerializedName("UserId")
    private int userId;

    @SerializedName("Username")
    private String username;

    @SerializedName("Email")
    private String email;

    @SerializedName("FullName")
    private String fullName;

    @SerializedName("Phone")
    private String phone;

    @SerializedName("DateOfBirth")
    private String dateOfBirth; // định dạng "yyyy-MM-dd"

    @SerializedName("Gender")
    private String gender;

    @SerializedName("CreatedAt")
    private Date createdAt;

    @SerializedName("TotalBookings")
    private int totalBookings;

    public UserProfileDto() {
    }

    public UserProfileDto(int userId, String username, String email, String fullName, String phone, String dateOfBirth, String gender, Date createdAt, int totalBookings) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.createdAt = createdAt;
        this.totalBookings = totalBookings;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }
}
