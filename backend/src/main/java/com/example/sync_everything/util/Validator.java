package com.example.sync_everything.util;

import com.example.sync_everything.controller.VerifyController;

import java.util.Objects;

/**
 * @author ForeverDdB
 * @ClassName Validator
 * @Description 校验工具类
 * @createTime 2022年 09月18日 17:16
 **/
public class Validator {
    public static Boolean isPasswordValid(String password) {
        if (password == null || password.equals("")) return false;
        if (password.length() < 6 || password.length() > 20) return false;
//        if ()正则匹配
        return true;
    }

    public static Boolean isUsernameValid(String username) {
        if (username == null || username.equals("")) return false;
        if (username.length() < 4 || username.length() > 20) return false;
        return true;
    }

    public static Boolean isCodeValid(String code, String token) {
        if (code == null || code.equals("") || code.length() != VerificationCodeUtil.CODE_COUNT) return false;
        if (token == null || token.equals("")) return false;
        if (JWTUtil.parseTokenWithSecret(token, code.toUpperCase()) == null) return false;
        String realCode = Redis.get(VerifyController.VERIFY_CODE_PREFIX + token);
        if (Objects.isNull(realCode) || !Objects.equals(realCode.toUpperCase(), code.toUpperCase())) return false;
        Long res = Redis.del(VerifyController.VERIFY_CODE_PREFIX + token);
        if (Objects.isNull(res) || res <= 0) return false;
        return true;
    }
}
