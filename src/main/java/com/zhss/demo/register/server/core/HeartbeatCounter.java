package com.zhss.demo.register.server.core;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author:
 * @Description: 心跳测量计数器
 * @Date: 2020/10/24 9:19
 * @Version: 1.0
 */
public class HeartbeatCounter {

    private static HeartbeatCounter instance = new HeartbeatCounter();

    private HeartbeatCounter(){
        Daemon daemon = new Daemon();
        daemon.setDaemon(true);
        daemon.start();
    }

    public static HeartbeatCounter getInstance() {
        return instance;
    }

    // 最近一分钟的心跳次数
    private AtomicLong latestMinuteHeartbeatRate = new AtomicLong(0);

    // 最近一分钟的时间戳
    private long latestMinuteTimestamp = System.currentTimeMillis();

    public void increment(){
        latestMinuteHeartbeatRate.incrementAndGet();
    }

    private class Daemon extends Thread {
        @Override
        public void run() {
            while(true) {
                try {
                    synchronized(HeartbeatCounter.class) {
                        long currentTime = System.currentTimeMillis();
                        if(currentTime - latestMinuteTimestamp > 60 * 1000) {
                            latestMinuteHeartbeatRate.set(0);
                            latestMinuteTimestamp = currentTime;
                        }
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public long get() {
        return latestMinuteHeartbeatRate.get();
    }
}
