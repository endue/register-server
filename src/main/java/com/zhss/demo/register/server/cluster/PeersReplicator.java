package com.zhss.demo.register.server.cluster;

import com.zhss.demo.register.server.web.AbstractRequest;
import com.zhss.demo.register.server.web.CancelRequest;
import com.zhss.demo.register.server.web.HeartbeatRequest;
import com.zhss.demo.register.server.web.RegisterRequest;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author:
 * @Description: 集群同步组件
 * @Date: 2020/11/4 22:31
 * @Version: 1.0
 */
public class PeersReplicator {

    private static final PeersReplicator instance = new PeersReplicator();

    private PeersReplicator() {
    }

    public static PeersReplicator getInstance() {
        return instance;
    }

    // 第一层队列
    private ConcurrentLinkedQueue<AbstractRequest> acceptorQueue = new ConcurrentLinkedQueue();

    /**
     * 同步服务注册请求
     */
    public void replicateRegister(RegisterRequest request) {
        acceptorQueue.offer(request);
    }

    /**
     * 同步服务下线请求
     */
    public void replicateCancel(CancelRequest request) {
        acceptorQueue.offer(request);
    }

    /**
     * 同步发送心跳请求
     */
    public void replicateHeartbeat(HeartbeatRequest request) {
        acceptorQueue.offer(request);
    }
}
