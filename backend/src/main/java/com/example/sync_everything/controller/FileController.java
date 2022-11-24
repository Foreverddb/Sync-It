package com.example.sync_everything.controller;

import com.auth0.jwt.interfaces.Claim;
import com.example.sync_everything.entity.file.SegmentFile;
import com.example.sync_everything.entity.file.SegmentFileDto;
import com.example.sync_everything.entity.file.SegmentFileVo;
import com.example.sync_everything.response.Response;
import com.example.sync_everything.response.ResponseCode;
import com.example.sync_everything.service.FileService;
import com.example.sync_everything.util.Dozer;
import com.example.sync_everything.util.FileUtil;
import com.example.sync_everything.util.JWTUtil;
import com.example.sync_everything.util.Redis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    public static final String FILE_PREFIX = "file-data:";

    @Autowired
    private FileService fileService;

    @Value("${file.savePath}")
    private String savePath;

    @Value("${file.segmentSize}")
    private int segmentSize;

    @GetMapping("/check")
    public Response checkFile(@RequestParam("key") String key) {
        SegmentFile segmentFile = fileService.queryByKey(key);
        if (segmentFile == null) {
            return Response.builder()
                    .code(ResponseCode.COMMON_SUCCESS)
                    .build();
        }
        SegmentFileVo segmentFileVo = Dozer.convert(segmentFile, SegmentFileVo.class);
        if (segmentFileVo != null) {
            segmentFileVo.setPath(FileUtil.createSaveFileName(segmentFileVo.getKey(), segmentFileVo.getFileName()));
        }
        return Response.builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(segmentFileVo)
                .build();
    }

    @PostMapping("/post")
    public Response postFile(@RequestParam("file") MultipartFile multipartFile,
                             @RequestParam("key") String key,
                             @RequestParam("size") int fileSize,
                             @RequestParam("segmentIndex") int segmentIndex,
                             @RequestParam("segmentTotal") int segmentTotal,
                             @RequestParam("fileName") String fileName,
                             @RequestParam("expireDay") int expireDay,
                             @RequestHeader("token") String token) throws MultipartException {
        Map<String, Claim> claims = JWTUtil.parseToken(token);
        assert claims != null;
        String uid = claims.get("uid").asString();

        if (multipartFile.isEmpty()) {
            return Response.builder()
                    .code(ResponseCode.COMMON_FAIL)
                    .msg("文件内容为空")
                    .build();
        }
        SegmentFile segmentFile = fileService.queryByKey(key);
        if (Objects.isNull(segmentFile)) {
            String saveFileName = FileUtil.createSaveFileName(key, fileName);
            SegmentFile file = new SegmentFile();
            file.setKey(key);
            file.setFileName(fileName);
            file.setFilePath(savePath + saveFileName);
            int total = fileSize / segmentSize;
            if (fileSize % segmentSize != 0) {
                total++;
            }
            if (total != segmentTotal) {
                throw new MultipartException("文件参数出错！");
            }
            file.setSegmentTotal(total);
            file.setSize(fileSize);
            file.setSegmentIndex(0);
            file.setUserId(Long.parseLong(uid));
            if (fileService.createSegmentFile(file) <= 0) {
                throw new MultipartException("文件参数出错！");
            }
            segmentFile = fileService.queryByKey(key);
        }
        if (segmentFile.getUserId() != Long.parseLong(uid)) {
            throw new MultipartException("您暂无权限更改此文件");
        }
        segmentFile.setSegmentIndex(segmentIndex);
        fileService.saveSegment(multipartFile, segmentFile);
        fileService.updateSegmentFile(Dozer.convert(segmentFile, SegmentFileDto.class));
        if (segmentFile.getSegmentIndex() == segmentFile.getSegmentTotal()) {
            if (fileService.mergeSegment(segmentFile)) {
                SegmentFile finalSegmentFile = segmentFile;
                new Thread(() -> fileService.deleteSegments(finalSegmentFile)).start();
            }
            Redis.zAdd(FILE_PREFIX + uid,
                    Dozer.toJsonString(segmentFile),
                    Double.parseDouble(String.valueOf(new Date().getTime() + (long) expireDay * 24 * 60 * 60 * 1000)));
        }
        SegmentFileVo segmentFileVo = Dozer.convert(segmentFile, SegmentFileVo.class);
        if (segmentFileVo != null) {
            segmentFileVo.setPath(FileUtil.createSaveFileName(segmentFileVo.getKey(), segmentFileVo.getFileName()));
        }
        return Response.builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(segmentFileVo)
                .build();
    }

    @PostMapping("/delete")
    public Response deleteFile(@RequestParam("key") String key,
                               @RequestHeader("token") String token) {
        Map<String, Claim> claims = JWTUtil.parseToken(token);
        assert claims != null;
        String uid = claims.get("uid").asString();

        SegmentFile segmentFile = fileService.queryByKey(key);
        if (segmentFile == null) {
            throw new MultipartException("文件不存在！");
        }
        Redis.zAdd(FILE_PREFIX + uid,
                Dozer.toJsonString(segmentFile),
                0);
        return Response.builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }
}
