package com.example.sync_everything.interceptor;

import com.auth0.jwt.interfaces.Claim;
import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.example.sync_everything.entity.user.User;
import com.example.sync_everything.exception.LoginException;
import com.example.sync_everything.service.UserService;
import com.example.sync_everything.util.Dozer;
import com.example.sync_everything.util.JWTUtil;
import com.example.sync_everything.util.Redis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * @author ForeverDdB
 * @ClassName SocketAuthInterceptor
 * @Description
 * @createTime 2022年 11月11日 18:44
 **/
@Slf4j
public class SocketAuthInterceptor implements AuthorizationListener {
    public static final String USER_PREFIX = "login-user:";
    public static final long EXPIRE_TIME = JWTUtil.EXPIRE_DAY * 24 * 60 * 60;
    public static final long EXPIRE_TIME_MILL = JWTUtil.EXPIRE_DAY * 24 * 60 * 60 * 1000;

    @Autowired
    private UserService userService;

    public boolean auth(HandshakeData handshakeData) {

        String token = null;
        token = handshakeData.getSingleUrlParam("token");
        Map<String, Claim> claims = JWTUtil.parseToken(token);
        if (!Objects.isNull(claims)) {
            String uid = claims.get("uid").asString();
            String loginData = Redis.get(USER_PREFIX + uid);
            if (!Objects.isNull(loginData)) {
                Long ttl = Redis.ttl(USER_PREFIX + uid);
                if (!Objects.isNull(ttl) && ttl > 0 && ttl <= (EXPIRE_TIME / 6)) {
                    String res = Redis.set(USER_PREFIX + uid, loginData, EXPIRE_TIME);
                }
                return true;
            } else {
                Redis.del(USER_PREFIX + uid);
            }
        }
        log.info("连接认证失败！");
        return false;
    }

    @Override
    public boolean isAuthorized(HandshakeData handshakeData) {
        return this.auth(handshakeData);
    }
}
