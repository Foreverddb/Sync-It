package com.example.sync_everything.service;

import com.example.sync_everything.entity.file.SegmentFile;
import com.example.sync_everything.entity.file.SegmentFileDto;
import com.example.sync_everything.entity.file.UsersFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    SegmentFile queryByKey(String key);

    int createSegmentFile(SegmentFile file);

    int updateSegmentFile(SegmentFileDto file);

    Boolean exists(String key);

    Boolean saveSegment(MultipartFile file, SegmentFile info);

    Boolean mergeSegment(SegmentFile info);

    Boolean deleteSegments(SegmentFile info);

    int deleteSegmentFile(String key);

    List<UsersFile> countUsersFile();
}
