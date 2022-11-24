package com.example.sync_everything.entity;

import lombok.Getter;

/**
 * @author ForeverDdB
 * @ClassName Events
 * @Description
 * @createTime 2022年 11月11日 22:21
 **/
@Getter
public enum Events {
    CLIPBOARD_ADD("clipboardAdd"),
    GET_ALL_DEVICES("getAllDevices"),
    GET_ALL_CLIPBOARDS("getAllClipboards"),

    GET_ALL_FILES("getAllFiles")
    ;
    private String event;
    Events(String event) {
        this.event = event;
    }
}
