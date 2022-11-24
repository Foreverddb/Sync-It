package com.example.sync_everything.controller;

import com.example.sync_everything.entity.verification.Code;
import com.example.sync_everything.entity.verification.CodeVo;
import com.example.sync_everything.response.Response;
import com.example.sync_everything.response.ResponseCode;
import com.example.sync_everything.util.Dozer;
import com.example.sync_everything.util.JWTUtil;
import com.example.sync_everything.util.Redis;
import com.example.sync_everything.util.VerificationCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author ForeverDdB
 * @ClassName VerifyController
 * @Description 一些安全验证操作api
 * @createTime 2022年 09月18日 22:49
 **/
@Slf4j
@RestController
@RequestMapping("/verify")
public class VerifyController {
    private final static long CODE_EXPIRE_TIME = 60;
    private final static long CODE_EXPIRE_TIME_MILL = CODE_EXPIRE_TIME * 1000;
    public final static String VERIFY_CODE_PREFIX = "verify-code:";

    @Resource
    private HttpServletRequest httpServletRequest;

    @GetMapping("/getCode")
    public Response getCode() {
        String userAgent = httpServletRequest.getHeader("User-Agent");
        String host = httpServletRequest.getHeader("Host");
        HashMap<String, String> payload = new HashMap<>();
        payload.put("userAgent", userAgent);
        payload.put("host", host);
        payload.put("time", Long.toString(System.currentTimeMillis()));
        Code code;
        try {
            code = VerificationCodeUtil.generateVerificationCode(200, 50);
            String token = JWTUtil.generateTokenWithSecret(payload, CODE_EXPIRE_TIME_MILL, code.getCode().toUpperCase());
            code.setToken(token);
            String res = Redis.set(VERIFY_CODE_PREFIX + token, code.getCode(), CODE_EXPIRE_TIME);
            if (!Objects.equals(res, "OK")) {
                throw new Exception();
            }
            log.info(code.getCode());
            return Response.builder()
                    .code(ResponseCode.COMMON_SUCCESS)
                    .data(Dozer.convert(code, CodeVo.class))
                    .build();
        } catch (Exception e) {
            return Response.builder()
                    .code(ResponseCode.COMMON_SERVER_ERROR)
                    .build();
        }
    }

}
