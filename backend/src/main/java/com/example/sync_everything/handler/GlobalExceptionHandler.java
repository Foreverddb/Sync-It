package com.example.sync_everything.handler;

import com.example.sync_everything.exception.LoginException;
import com.example.sync_everything.response.Response;
import com.example.sync_everything.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

/**
 * @author ForeverDdB
 * @ClassName GlobalExceptionHandler
 * @Description 处理全局错误
 * @createTime 2022年 09月17日 23:46
 **/
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public @ResponseBody
    ResponseEntity<Response> MissingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e){
        Response res=Response
                .builder()
                .code(ResponseCode.COMMON_PARAMS_ERROR)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public @ResponseBody
    ResponseEntity<Response> MethodArgumentNotValidExceptionHandler(MethodArgumentTypeMismatchException e){
        Response res=Response
                .builder()
                .code(ResponseCode.COMMON_PARAMS_ERROR)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public @ResponseBody
    ResponseEntity<Response> HttpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e){
        Response res=Response
                .builder()
                .code(ResponseCode.COMMON_PARAMS_ERROR)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(LoginException.class)
    public @ResponseBody
    ResponseEntity<Response> LoginExceptionHandler(LoginException e) {
        Response res=Response
                .builder()
                .code(ResponseCode.COMMON_AUTH_ERROR)
                .msg(e.getMsg())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }

    @ExceptionHandler(MultipartException.class)
    public @ResponseBody
    ResponseEntity<Response> MultipartExceptionHandler(MultipartException e) {
        e.printStackTrace();
        Response res = Response.builder()
                .code(ResponseCode.COMMON_FAIL)
                .msg(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
