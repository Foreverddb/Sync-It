package com.example.sync_everything.controller.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.sync_everything.handler.SocketIOHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ForeverDdB
 * @ClassName SocketIOServerRunner
 * @Description
 * @createTime 2022年 11月11日 18:19
 **/
@Slf4j
@Component
public class SocketIOServerRunner implements ApplicationListener<ContextRefreshedEvent> {
    @Resource
    private SocketIOHandler socketIOHandler;

    @Resource
    private SocketIOServer socketIOServer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null){
            socketIOServer.addListeners(socketIOHandler);
            socketIOServer.start();
            log.info("socket-io启动成功!");
        }
    }

    @Autowired
    public SocketIOServerRunner(SocketIOServer server) {
        this.socketIOServer = server;
    }
}
