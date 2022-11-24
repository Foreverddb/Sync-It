package com.example.sync_everything.service.impl;

import com.example.sync_everything.dao.FileDao;
import com.example.sync_everything.entity.file.SegmentFile;
import com.example.sync_everything.entity.file.SegmentFileDto;
import com.example.sync_everything.entity.file.UsersFile;
import com.example.sync_everything.service.FileService;
import com.example.sync_everything.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private FileDao fileDao;

    public SegmentFile queryByKey(String key) {
        return fileDao.queryByKey(key);
    }

    @Override
    public int createSegmentFile(SegmentFile file) {
        return fileDao.createSegmentFile(file);
    }

    @Override
    public int updateSegmentFile(SegmentFileDto file) {
        return fileDao.updateSegmentFile(file);
    }

    @Override
    public Boolean exists(String key) {
        return fileDao.exists(key);
    }

    @Override
    public Boolean saveSegment(MultipartFile file, SegmentFile info) {
        String segmentFilePath = FileUtil.getSegmentName(info.getFilePath(), info.getSegmentIndex());
        File dest = new File(segmentFilePath);
        log.info(dest.getPath());
        if (!dest.getParentFile().exists()) {
            if (!dest.getParentFile().mkdir()) {
                throw new MultipartException("服务器内部错误");
            }
        }
        try {
            if (!dest.exists()) {
                if (!dest.createNewFile()) {
                    throw new MultipartException("服务器内部错误");
                }
            } else {
                if (!dest.delete()) {
                    throw new MultipartException("服务器内部错误");
                }
                if (!dest.createNewFile()) {
                    throw new MultipartException("服务器内部错误");
                }
            }
            file.transferTo(dest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MultipartException("服务器内部错误");
        }
        return true;
    }

    @Override
    public Boolean mergeSegment(SegmentFile info) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream;
        byte[] bytes = new byte[10 * 1024 * 1024];
        try {
            File optFile = new File(info.getFilePath());
            fileOutputStream = new FileOutputStream(optFile, true);
            int len;
            for (int i = 1;i <= info.getSegmentTotal();i ++) {
                String segmentPath = FileUtil.getSegmentName(info.getFilePath(), i);
                fileInputStream = new FileInputStream(segmentPath);
                while ((len = fileInputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, len);
                }
            }
            fileOutputStream.close();
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new MultipartException("服务器内部错误");
        }
        return true;
    }

    @Override
    public Boolean deleteSegments(SegmentFile info) {
        System.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        int[] visited = new int[info.getSegmentTotal()];
        int finished = 0;
        while (finished < info.getSize()) {
            for (int i = 0; i < info.getSegmentTotal();i ++) {
                if (visited[i] == 0) {
                    String segmentPath = FileUtil.getSegmentName(info.getFilePath(), i + 1);
                    File file = new File(segmentPath);
                    if (file.delete()) {
                        finished ++;
                        visited[i] = 1;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int deleteSegmentFile(String key) {
        return fileDao.deleteSegmentFile(key);
    }

    @Override
    public List<UsersFile> countUsersFile() {
        return fileDao.countUsersFile();
    }
}
