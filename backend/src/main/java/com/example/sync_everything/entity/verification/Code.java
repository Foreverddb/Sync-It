package com.example.sync_everything.entity.verification;

import lombok.Data;

/**
 * @author ForeverDdB
 * @ClassName Code
 * @Description 验证码
 * @createTime 2022年 09月19日 21:51
 **/
@Data
public class Code {
    String token;
    String codeImage;
    String code;
}

