package com.example.sync_everything.entity;

import com.example.sync_everything.util.Dozer;
import lombok.Data;

/**
 * @author ForeverDdB
 * @ClassName CommonData
 * @Description
 * @createTime 2022年 11月11日 22:05
 **/
@Data
public class CommonData<T> {
    private String event;
    private T data;
}
