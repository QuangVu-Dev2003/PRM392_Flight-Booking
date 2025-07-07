package com.prm.flightbooking.dto.user;

import com.google.gson.annotations.SerializedName;

public class RegisterUserDto {
    @SerializedName("Username")
    private String username;

    @SerializedName("Email")
    private String email;

    @SerializedName("Password")
    private String password;

    @SerializedName("FullName")
    private String fullName;

    @SerializedName("Phone")
    private String phone;

    @SerializedName("DateOfBirth")
    private String dateOfBirth; // dùng String định dạng "yyyy-MM-dd"

    @SerializedName("Gender")
    private String gender;

    public RegisterUserDto() {
    }

    public RegisterUserDto(String username, String email, String password, String fullName, String phone, String dateOfBirth, String gender) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
