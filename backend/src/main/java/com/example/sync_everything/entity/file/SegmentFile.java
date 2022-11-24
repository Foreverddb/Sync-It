package com.example.sync_everything.entity.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SegmentFile {

    private int id;

    private long userId;

    private String key;

    private String fileName;

    private String filePath;

    private int size;

    private int segmentIndex;

    private int segmentTotal;

    private Date createdAt;

    private Date updatedAt;
}
