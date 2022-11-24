package com.example.sync_everything.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ForeverDdB
 * @ClassName JWTUtil
 * @Description jwt工具类
 * @createTime 2022年 09月18日 23:04
 **/
@Slf4j
@Configuration
public class JWTUtil {
    public final static long EXPIRE_DAY = 180;
    public final static long EXPIRE_TIME = 24 * 60 * 60 * 1000;
    private static String SECRET_KEY;

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Autowired
    public void setSecretKey() {
        JWTUtil.SECRET_KEY = secretKey;
    }

    public static String generateToken(HashMap<String, String> payload,long expire_time) {
        Date expire_at = new Date(System.currentTimeMillis() + (expire_time != 0 ? expire_time : EXPIRE_TIME));
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        JWTCreator.Builder builder = JWT.create();
        for (String key:
             payload.keySet()) {
            builder = builder.withClaim(key, payload.get(key));
        }
        return builder.withExpiresAt(expire_at).sign(algorithm);
    }

    public static String generateTokenWithSecret(HashMap<String, String> payload,long expire_time, String secret) {
        Date expire_at = new Date(System.currentTimeMillis() + (expire_time != 0 ? expire_time : EXPIRE_TIME));
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY + secret);
        JWTCreator.Builder builder = JWT.create();
        for (String key:
                payload.keySet()) {
            builder = builder.withClaim(key, payload.get(key));
        }
        return builder.withExpiresAt(expire_at).sign(algorithm);
    }

    public static Map<String, Claim> parseToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = jwtVerifier.verify(token);
            return jwt.getClaims();
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<String, Claim> parseTokenWithSecret(String token, String secret) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY + secret);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = jwtVerifier.verify(token);
            return jwt.getClaims();
        } catch (Exception e) {
            return null;
        }
    }
}
