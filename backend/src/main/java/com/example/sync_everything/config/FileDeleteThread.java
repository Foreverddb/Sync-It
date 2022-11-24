package com.example.sync_everything.config;

import ch.qos.logback.core.util.TimeUtil;
import com.example.sync_everything.controller.FileController;
import com.example.sync_everything.entity.file.SegmentFile;
import com.example.sync_everything.entity.file.UsersFile;
import com.example.sync_everything.service.FileService;
import com.example.sync_everything.util.Dozer;
import com.example.sync_everything.util.Redis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class FileDeleteThread implements Runnable{
    @Resource
    private FileService fileService;

    @Autowired
    public void init() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            List<UsersFile> usersFiles = fileService.countUsersFile();
            LinkedList<Long> users = new LinkedList<>();
            for (UsersFile usersFile:
                usersFiles) {
                if (usersFile.getCount() > 0) {
                    users.add(usersFile.getUserId());
                }
            }
            while (users.size() > 0) {
                for (Long uid:
                    users) {
                    Set<String> stringList = Redis.zRangeByScore(FileController.FILE_PREFIX + uid, 0, new Date().getTime());
                    LinkedList<SegmentFile> segmentFiles = new LinkedList<>();
                    if (stringList != null) {
                        for (String json:
                                stringList) {
                            segmentFiles.add(Dozer.toEntity(json, SegmentFile.class));
                        }
                        while (segmentFiles.size() > 0) {
                            for (SegmentFile segmentFile:
                                    segmentFiles) {
                                File file = new File(segmentFile.getFilePath());
                                if (file.exists()) {
                                    if (file.delete()){
                                        if (fileService.deleteSegmentFile(segmentFile.getKey()) > 0) {
                                            segmentFiles.remove(segmentFile);
                                            Redis.zRem(FileController.FILE_PREFIX + uid, Dozer.toJsonString(segmentFile));
                                            log.info("已删除用户id" + segmentFile.getUserId() + "的文件：" + segmentFile.getFileName());
                                        }
                                    }
                                }
                            }
                        }
                        users.remove(uid);
                    }
                }
            }
            try {
                TimeUnit.MINUTES.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
