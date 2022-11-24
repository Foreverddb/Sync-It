package com.example.sync_everything.entity.user;

import lombok.Data;

/**
 * @author ForeverDdB
 * @ClassName RegisterUserForm
 * @Description 注册信息
 * @createTime 2022年 09月18日 21:31
 **/
@Data
public class RegisterUserForm {
    String username;
    String password;
    String code;
    String token;
}
