package com.example.sync_everything.interceptor;

import com.auth0.jwt.interfaces.Claim;
import com.example.sync_everything.annotation.LoginRequired;
import com.example.sync_everything.entity.user.User;
import com.example.sync_everything.exception.LoginException;
import com.example.sync_everything.service.UserService;
import com.example.sync_everything.util.Dozer;
import com.example.sync_everything.util.JWTUtil;
import com.example.sync_everything.util.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

/**
 * @author ForeverDdB
 * @ClassName LoginInterceptor
 * @Description 验证登录状态的拦截器
 * @createTime 2022年 09月21日 16:08
 **/
@Configuration
public class LoginInterceptor implements HandlerInterceptor {
    public static final String USER_PREFIX = "login-user:";
    public static final long EXPIRE_TIME = JWTUtil.EXPIRE_DAY * 24 * 60 * 60;
    public static final long EXPIRE_TIME_MILL = JWTUtil.EXPIRE_DAY * 24 * 60 * 60 * 1000;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LoginRequired annotation = handlerMethod.getMethod().getAnnotation(LoginRequired.class);
            if (Objects.isNull(annotation)) return true;

            String token = null;
            token = request.getHeader("token");
            Map<String, Claim> claims = JWTUtil.parseToken(token);
            if (!Objects.isNull(claims)) {
                String uid = claims.get("uid").asString();
                String loginData = Redis.get(USER_PREFIX + uid);
                if (!Objects.isNull(loginData)) {
                    User user =  userService.queryByUser(Dozer.toEntity(loginData, User.class));
                    if (!Objects.isNull(user)) {
                        Long ttl = Redis.ttl(USER_PREFIX + uid);
                        if (!Objects.isNull(ttl) && ttl > 0 && ttl <= (EXPIRE_TIME / 6)) {
                            String res = Redis.set(USER_PREFIX + uid, Dozer.toJsonString(user), EXPIRE_TIME);
                        }
                        return true;
                    } else {
                        Redis.del(USER_PREFIX + uid);
                    }
                }
            }
            throw new LoginException();
        }
//        if (handler instanceof ResourceHttpRequestHandler) {
//            return true;
//        }
        return true;
    }
}
