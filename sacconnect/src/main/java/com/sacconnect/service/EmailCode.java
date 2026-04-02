package com.sacconnect.service;

import java.util.Random;

public class EmailCode {

    private static EmailCode instance;

    private EmailCode() {}

    public static synchronized EmailCode getInstance() {
        if (instance == null) {
            instance = new EmailCode();
        }
        return instance;
    }
    public String generateCode() {
        Random rand = new Random();
        int code = 100000 + rand.nextInt(900000); // 6-digit code
        return String.valueOf(code);
    }
}