package com.example.sync_everything.controller;

import com.example.sync_everything.annotation.LoginRequired;
import com.example.sync_everything.entity.user.LoginUserForm;
import com.example.sync_everything.entity.user.LoginUserResp;
import com.example.sync_everything.entity.user.RegisterUserForm;
import com.example.sync_everything.entity.user.User;
import com.example.sync_everything.interceptor.LoginInterceptor;
import com.example.sync_everything.response.Response;
import com.example.sync_everything.response.ResponseCode;
import com.example.sync_everything.service.UserService;
import com.example.sync_everything.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author ForeverDdB 835236331@qq.com
 * @ClassName MainController
 * @Description 用户管理
 * @createTime 2022年 09月03日 15:59
 **/
@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @Resource
    private HttpServletResponse response;

    @PostMapping("/login")
    public Response login(@RequestBody LoginUserForm loginUserForm) {
        // 基础校验
        if (!Validator.isUsernameValid(loginUserForm.getUsername())) {
            return Response.builder()
                    .code(ResponseCode.USERNAME_NOT_VALID)
                    .build();
        }
        if (!Validator.isPasswordValid(loginUserForm.getPassword())) {
            return Response.builder()
                    .code(ResponseCode.PASSWORD_NOT_VALID)
                    .build();
        }
        if (!Validator.isCodeValid(loginUserForm.getCode(), loginUserForm.getToken())) {
            return Response.builder()
                    .code(ResponseCode.CODE_NOT_VALID)
                    .build();
        }
        // 校验用户是否存在
        User user = userService.queryByUser(Dozer.convert(loginUserForm, User.class));
        if (Objects.isNull(user) || !Security.bCryptPasswordMatch(loginUserForm.getPassword(), user.getPassword())) {
            return Response.builder()
                    .code(ResponseCode.USER_LOGIN_FAIL)
                    .build();
        }
        // 生成token
        HashMap<String, String> userInfo = new HashMap<>();
        userInfo.put("username", user.getUsername());
        userInfo.put("uid", Long.toString(user.getId()));
        String token = JWTUtil.generateToken(userInfo, LoginInterceptor.EXPIRE_TIME_MILL);
        if (Objects.isNull(token)) {
            return Response.builder()
                    .code(ResponseCode.COMMON_SERVER_ERROR)
                    .build();
        }
        // 登录数据存入redis
        String res = Redis.set(LoginInterceptor.USER_PREFIX + user.getId(), Dozer.toJsonString(user), LoginInterceptor.EXPIRE_TIME);
        if (!Objects.equals(res, "OK")) {
            return Response.builder()
                    .code(ResponseCode.COMMON_SERVER_ERROR)
                    .build();
        }
        // 登录成功，传回信息和token
        response.addCookie(new Cookie("token", token));
        LoginUserResp userResp = new LoginUserResp();
        userResp.setUserId(user.getId());
        userResp.setUsername(user.getUsername());
        userResp.setToken(token);
        return Response.builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(userResp)
                .build();
    }

    @PostMapping("/register")
    public Response register(@RequestBody RegisterUserForm user) {
        // 基础校验
        if (!Validator.isUsernameValid(user.getUsername())) {
            return Response.builder()
                    .code(ResponseCode.USERNAME_NOT_VALID)
                    .build();
        }
        if (!Validator.isPasswordValid(user.getPassword())) {
            return Response.builder()
                    .code(ResponseCode.PASSWORD_NOT_VALID)
                    .build();
        }
        if (!Validator.isCodeValid(user.getCode(), user.getToken())) {
            return Response.builder()
                    .code(ResponseCode.CODE_NOT_VALID)
                    .build();
        }
        User u = Dozer.convert(user, User.class);
        if (userService.queryByUser(u) != null) {
            return Response.builder()
                    .code(ResponseCode.USER_ALREADY_EXISTS)
                    .build();
        }
        u.setPassword(Security.bCryptPasswordEncode(u.getPassword()));
        int res = userService.createUser(u);
        if (res > 0) return Response.builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .len(res)
                .build();
        return Response.builder()
                .code(ResponseCode.COMMON_FAIL)
                .build();
    }

    @LoginRequired
    @GetMapping("/query")
    public Response queryUser(@RequestParam("id") Long id){
        if (!userService.exists(id)) {
            return Response.builder()
                    .code(ResponseCode.USER_NOT_FOUND)
                    .build();
        }
        User user = userService.queryById(id);
        user.setPassword(null);
        return Response.builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .len(1)
                .data(user)
                .build();
    }

//    @LoginRequired
//    @PostMapping("/update")
//    public Response updateUser(@RequestBody UpdateUserForm user) {
//        if (!userService.exists(user.getId())) {
//            return Response.builder()
//                    .code(ResponseCode.USER_NOT_FOUND)
//                    .build();
//        }
//        if (user.getPassword() != null && !user.getPassword().isBlank()) {
//            if (!Validator.isPasswordValid(user.getPassword())) {
//                return Response.builder()
//                        .code(ResponseCode.PASSWORD_NOT_VALID)
//                        .build();
//            }
//            user.setPassword(Security.bCryptPasswordEncode(user.getPassword()));
//        }
//        int res = userService.updateUser(Dozer.convert(user, User.class));
//        user.setPassword(null);
//        if (res > 0) return Response.builder()
//                .code(ResponseCode.COMMON_SUCCESS)
//                .len(res)
//                .build();
//        return Response.builder()
//                .code(ResponseCode.COMMON_FAIL)
//                .len(0)
//                .build();
//    }

    @PostMapping("/delete")
    public Response deleteUser(@RequestParam("id") Long id) {
        if (!userService.exists(id)) {
            return Response.builder()
                    .code(ResponseCode.USER_NOT_FOUND)
                    .build();
        }
        int res = userService.deleteUser(id);
        if (res > 0) return Response.builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .len(res)
                .build();
        return Response.builder()
                .code(ResponseCode.COMMON_FAIL)
                .len(0)
                .build();
    }
}

