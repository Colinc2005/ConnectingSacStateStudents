package com.sacconnect.dto.request;

public class VerifyUserRequest {
    private String email;
    private String code;

    public VerifyUserRequest() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}