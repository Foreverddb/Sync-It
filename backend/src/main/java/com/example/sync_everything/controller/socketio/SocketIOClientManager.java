package com.example.sync_everything.controller.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import com.example.sync_everything.entity.CommonData;
import com.example.sync_everything.entity.socketio.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author ForeverDdB
 * @ClassName SocketIOClientPool
 * @Description 用户连接池
 * @createTime 2022年 11月11日 18:26
 **/
@Component
public class SocketIOClientManager {
    private static final ConcurrentHashMap<Long, ConcurrentHashMap<String, SocketIOClient>> clients = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, SocketIOThread> threads = new ConcurrentHashMap<>();

    public static void addThread(Long id) {
        if (threads.get(id) == null) {
            SocketIOThread socketIOThread = new SocketIOThread(id);
            socketIOThread.start();
            threads.put(id, socketIOThread);
        }
    }

    public static int userSize() {
        return clients.size();
    }

    public static int deviceSize(Long id) {
        ConcurrentHashMap<String, SocketIOClient> user = clients.get(id);
        System.out.println(user.keySet().toString());
        if (user != null) {
            return clients.get(id).size();
        } else {
            return 0;
        }
    }

    public static LinkedList<Device> getAllDevices(Long id) {
        LinkedList<Device> list = new LinkedList<>();
        try {
            if (clients.get(id) != null && clients.get(id).size() > 0) {
                Set<String> name = clients.get(id).keySet();
                for (String deviceName:
                        name) {
                    Device device = new Device();
                    device.setName(deviceName);
                    if (Pattern.matches("^.*?(win|windows|Win|Windows|mac|Mac|MacOs).*?$", deviceName)) {
                        device.setType("pc");
                    } else {
                        device.setType("phone");
                    }
                    list.add(device);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void add(Long id, String deviceId,SocketIOClient client) {
        ConcurrentHashMap<String, SocketIOClient> user = clients.get(id);
        if (user != null) {
            user.put(deviceId, client);
        } else {
            clients.put(id, new ConcurrentHashMap<String, SocketIOClient>() {
                {
                    put(deviceId, client);
                }
            });
        }
        addThread(id);
    }

    public static void removeAndClose(Long id, String deviceId) {
        SocketIOClient client = clients.get(id).get(deviceId);
        client.disconnect();
        clients.get(id).remove(deviceId);
        if (clients.get(id).size() <= 0) {
            clients.remove(id);
            threads.remove(id);
        }
    }

    public static void boardCast(Long id, String event, Object data) {
        ConcurrentHashMap<String, SocketIOClient> user = clients.get(id);
        if (user != null) {
            for (SocketIOClient client:
                user.values()) {
                client.sendEvent(event, data);
            }
        }
    }

    public static void pushTask(Long id, CommonData data) {
        SocketIOThread thread = threads.get(id);
        if (thread != null) {
            thread.pushTask(data);
        }
    }
}
