package com.example.sync_everything.controller.socketio;

import com.example.sync_everything.entity.CommonData;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author ForeverDdB
 * @ClassName SocketIOThread
 * @Description
 * @createTime 2022年 11月11日 20:45
 **/
public class SocketIOThread extends Thread{

    private Long id;
    private final ConcurrentLinkedQueue<CommonData<?>> clipboardQueue = new ConcurrentLinkedQueue<>();
    private final Object lock = new Object();

    public SocketIOThread(Long id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            while (clipboardQueue.size() > 0) {
                CommonData<?> data = clipboardQueue.poll();
                SocketIOClientManager.boardCast(this.id, data.getEvent(), data.getData());
            }
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void pushTask(CommonData<?> data) {
        clipboardQueue.add(data);
        synchronized (lock) {
            lock.notify();
        }
    }
}
