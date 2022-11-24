package com.example.sync_everything.entity.socketio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ForeverDdB
 * @ClassName Clipboard
 * @Description 剪贴板实体类
 * @createTime 2022年 11月11日 20:28
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Clipboard {

    private String text;

    private long time;

    private String from;
}
