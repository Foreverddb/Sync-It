package com.example.sync_everything.exception;

import lombok.Data;

/**
 * @author ForeverDdB
 * @ClassName LoginException
 * @Description
 * @createTime 2022年 09月21日 16:22
 **/
@Data
public class LoginException extends RuntimeException{
    private final String msg;
    public LoginException(String msg) {
        this.msg = msg;
    }

    public LoginException() {
        this.msg = "请先登陆";
    }
}
