package com.itheima.ssm.utils;

import org.springframework.aop.TargetSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordEncoderUtils {
    private static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    public static String encode(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    public static void main(String[] args) {
        String password = "123";
        String pwd = encode(password);
        System.out.println(pwd);
        System.out.println(pwd.length());
    }

}
