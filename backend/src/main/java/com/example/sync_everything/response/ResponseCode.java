package com.example.sync_everything.response;

import lombok.Getter;

/**
 * @author ForeverDdB
 * @ClassName ResponseCode
 * @Description
 * @createTime 2022年 09月16日 22:32
 **/
@Getter
public enum ResponseCode {
    COMMON_SUCCESS(0, "成功"),
    COMMON_FAIL(1, "错误"),
    /* 请求异常 */
    COMMON_PARAMS_ERROR(40000, "请求参数错误或缺失"),
    USER_NOT_FOUND(40001, "此用户不存在"),
    USER_ALREADY_EXISTS(40002, "用户名已存在"),
    USER_LOGIN_FAIL(40003, "用户名或密码错误"),
    USERNAME_NOT_VALID(40004, "用户名格式错误"),
    PASSWORD_NOT_VALID(40005, "密码格式错误"),
    CODE_NOT_VALID(40006, "验证码错误或已过期"),
    COMMON_AUTH_ERROR(401, "身份验证失败"),

    COMMON_SERVER_ERROR(50001, "服务器内部错误")
    ;
    private final int code;
    private final String message;
    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
