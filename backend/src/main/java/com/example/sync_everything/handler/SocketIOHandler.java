package com.example.sync_everything.handler;

import com.auth0.jwt.interfaces.Claim;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.example.sync_everything.controller.FileController;
import com.example.sync_everything.controller.socketio.SocketIOClientManager;
import com.example.sync_everything.entity.CommonData;
import com.example.sync_everything.entity.Events;
import com.example.sync_everything.entity.file.SegmentFile;
import com.example.sync_everything.entity.file.SegmentFileVo;
import com.example.sync_everything.entity.socketio.Clipboard;
import com.example.sync_everything.entity.socketio.Device;
import com.example.sync_everything.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * @author ForeverDdB
 * @ClassName SocketIOHandler
 * @Description
 * @createTime 2022年 11月11日 18:08
 **/
@Slf4j
@Component
public class SocketIOHandler {
    @Resource
    private SocketIOServer socketIOServer;

    @OnConnect
    public void onConnect(SocketIOClient client) {
        try {
            Map<String, Claim> claims = JWTUtil.parseToken(client.getHandshakeData().getSingleUrlParam("token"));
            assert claims != null;

            Long uid = Long.valueOf(claims.get("uid").asString());
            String username = claims.get("username").asString();
            String deviceId = client.getHandshakeData().getSingleUrlParam("device");

            client.set("uid", uid);
            client.set("username", username);
            client.set("deviceId", deviceId);

            SocketIOClientManager.add(uid, deviceId, client);
            log.info(uid + ":" + username + "的设备" + deviceId + " 连接成功。当前已连接设备数：" + socketIOServer.getAllClients().size());
        } catch (Exception e) {
            e.printStackTrace();
            client.disconnect();
        }
    }

    @OnEvent(value = "clipboardAdd")
    public void onClipBoard(SocketIOClient client, AckRequest request, Clipboard data) {
        Long uid = client.get("uid");
        CommonData<Clipboard> commonData = new CommonData<>();
        commonData.setEvent(Events.CLIPBOARD_ADD.getEvent());
        commonData.setData(data);
        if (data.getText() == null || data.getText().isBlank()) {
            return;
        }
        if (data.getText().equals(Redis.lIndex(PrefixUtil.getClipboardPrefix(uid), 0L))) {
            return;
        }
        Long len = Redis.lLen(PrefixUtil.getClipboardPrefix(uid));
        if (len != null && len >= 100) {
            String res = Redis.rPop(PrefixUtil.getClipboardPrefix(uid));
            log.info("列表过长，移除第一个数据" + res);
        }
        Redis.lPush(PrefixUtil.getClipboardPrefix(uid), Dozer.toJsonString(data));
        log.info("收到数据：" + commonData);
        SocketIOClientManager.pushTask(uid, commonData);
    }

    @OnEvent(value = "getAllClipboards")
    public void onGetAllClipboards(SocketIOClient client, AckRequest request, Clipboard data) {
        log.info("收到剪贴板云同步请求");
        Long uid = client.get("uid");
        CommonData<List<Clipboard>> commonData = new CommonData<>();
        commonData.setEvent(Events.GET_ALL_CLIPBOARDS.getEvent());

        List<String> clipboards = Redis.lRange(PrefixUtil.getClipboardPrefix(uid), 0, 99);
        List<Clipboard> clipboardsData = new LinkedList<>();
        if (clipboards != null) {
            for (String clipboard:
                    clipboards) {
                clipboardsData.add(Dozer.toEntity(clipboard, Clipboard.class));
            }
        }
        commonData.setData(clipboardsData);
        client.sendEvent(commonData.getEvent(), commonData.getData());
    }

    @OnEvent(value = "getAllDevices")
    public void onGetAllDevices(SocketIOClient client, AckRequest request) {
        log.info("收到设备列表请求");
        Long uid = client.get("uid");
        CommonData<LinkedList<Device>> commonData = new CommonData<>();
        LinkedList<Device> devices = SocketIOClientManager.getAllDevices(uid);
        commonData.setData(devices);
        commonData.setEvent(Events.GET_ALL_DEVICES.getEvent());
        client.sendEvent(commonData.getEvent(), commonData.getData());
    }

    @OnEvent(value = "getAllFiles")
    public void onGetAllFiles(SocketIOClient client, AckRequest request) {
        log.info("收到获取文件列表请求");
        Long uid = client.get("uid");
        CommonData<LinkedList<SegmentFileVo>> commonData = new CommonData<>();
        commonData.setEvent(Events.GET_ALL_FILES.getEvent());

        Long len = Redis.zLen(FileController.FILE_PREFIX + uid);
        if (len == null) {
            len = 0L;
        }
        Set<String> files = Redis.zRange(FileController.FILE_PREFIX + uid, 0, len - 1);
        LinkedList<SegmentFileVo> filesData = new LinkedList<>();
        if (files != null) {
            for (String file:
                    files) {
                Double score = Redis.zScore(FileController.FILE_PREFIX + uid, file);
                SegmentFileVo segmentFileVo = Dozer.convert(Dozer.toEntity(file, SegmentFile.class), SegmentFileVo.class);
                if (segmentFileVo != null) {
                    segmentFileVo.setScore(score);
                    segmentFileVo.setPath(FileUtil.createSaveFileName(segmentFileVo.getKey(), segmentFileVo.getFileName()));
                }
                filesData.add(segmentFileVo);
            }
        }
        commonData.setData(filesData);
        client.sendEvent(commonData.getEvent(), commonData.getData());
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        Long uid = client.get("uid");
        String deviceId = client.get("deviceId");
        String username = client.get("username");
        SocketIOClientManager.removeAndClose(uid, deviceId);
        log.info(uid + ":" + username + "的设备" + deviceId + " 已断开连接。当前已连接设备数：" + SocketIOClientManager.deviceSize(uid));
    }


}
