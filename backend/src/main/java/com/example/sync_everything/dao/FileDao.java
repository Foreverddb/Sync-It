package com.example.sync_everything.dao;

import com.example.sync_everything.entity.file.SegmentFile;
import com.example.sync_everything.entity.file.SegmentFileDto;
import com.example.sync_everything.entity.file.UsersFile;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface FileDao {
    SegmentFile queryByKey(@Param("key") String key);

    int createSegmentFile(SegmentFile file);

    int updateSegmentFile(SegmentFileDto file);

    Boolean exists(String key);

    int deleteSegmentFile(String key);

    List<UsersFile> countUsersFile();

}
