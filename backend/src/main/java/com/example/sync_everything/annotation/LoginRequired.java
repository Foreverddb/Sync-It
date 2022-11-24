package com.example.sync_everything.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ForeverDdB
 * @ClassName LoginRequired
 * @Description 要求登录
 * @createTime 2022年 09月21日 15:56
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
