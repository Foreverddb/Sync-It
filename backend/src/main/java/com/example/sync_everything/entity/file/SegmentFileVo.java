package com.example.sync_everything.entity.file;

import lombok.Data;

import java.util.Date;

@Data
public class SegmentFileVo {
    private String key;
    private int segmentIndex;
    private int segmentTotal;
    private int size;
    private String fileName;
    private String path;
    private Date updatedAt;
    private Double score;
}
