package com.prm.flightbooking.dto.user;

import com.google.gson.annotations.SerializedName;

public class LoginDto {
    @SerializedName("UsernameOrEmail")
    private String usernameOrEmail;

    @SerializedName("Password")
    private String password;

    public LoginDto() {
    }

    public LoginDto(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
