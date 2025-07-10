package com.prm.flightbooking.dto.user;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordDto {
    @SerializedName("email")
    private String email;

    @SerializedName("otpCode")
    private String otpCode;

    @SerializedName("newPassword")
    private String newPassword;

    public ResetPasswordDto(){

    }

    public ResetPasswordDto(String email, String otpCode, String newPassword) {
        this.email = email;
        this.otpCode = otpCode;
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
