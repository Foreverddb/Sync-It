package com.example.sync_everything.util;

/**
 * @author ForeverDdB
 * @ClassName PrefixUtil
 * @Description
 * @createTime 2022年 11月11日 20:38
 **/
public class PrefixUtil {
    public static String getClipboardPrefix(Long id) {
        return "clipboard" + ":" + id.toString();
    }
}
