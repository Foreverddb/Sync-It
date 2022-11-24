package com.example.sync_everything.entity.user;

import lombok.Data;

/**
 * @author ForeverDdB
 * @ClassName LoginUserForm
 * @Description
 * @createTime 2022年 09月20日 23:08
 **/
@Data
public class LoginUserForm {
    String username;
    String password;
    String code;
    String token;
}

