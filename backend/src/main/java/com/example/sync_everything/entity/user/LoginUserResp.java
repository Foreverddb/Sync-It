package com.example.sync_everything.entity.user;

import lombok.Data;

/**
 * @author ForeverDdB
 * @ClassName LoginUserResp
 * @Description
 * @createTime 2022年 09月21日 00:09
 **/
@Data
public class LoginUserResp {
    String token;
    String username;
    long userId;
}
