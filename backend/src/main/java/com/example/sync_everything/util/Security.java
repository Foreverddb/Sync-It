package com.example.sync_everything.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author ForeverDdB
 * @ClassName Security
 * @Description
 * @createTime 2022年 09月18日 16:05
 **/
public class Security {

    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     * @param password: 要加密的密码
     * @method bCryptPasswordEncode
     * @return java.lang.String
     *
     */
    public static String bCryptPasswordEncode(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    /**
     * 匹配加密后的密码
     * @param password: 要匹配的密码
     * @param encoded: 数据库中编码后的密码
     * @method bCryptPasswordMatch
     * @return java.lang.Boolean
     *
     */
    public static Boolean bCryptPasswordMatch(String password, String encoded) {
        return bCryptPasswordEncoder.matches(password, encoded);
    }
}
