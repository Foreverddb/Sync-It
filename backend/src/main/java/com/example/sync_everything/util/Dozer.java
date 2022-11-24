package com.example.sync_everything.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dozer.DozerBeanMapper;

/**
 * @author ForeverDdB
 * @ClassName Dozer
 * @Description dozer转换工具类
 * @createTime 2022年 09月17日 00:00
 **/
public class Dozer {

    /**
     * dozer转换的核心mapper对象
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T convert(Object obj, Class<T> tClass) {
        if (obj == null) return null;
        else return new DozerBeanMapper().map(obj, tClass);
    }

    public static <T> T toEntity(String str,Class<T> tClass){
        try {
            return objectMapper.readValue(str,tClass);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String toJsonString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        }catch (Exception e){
            return null;
        }
    }
}
