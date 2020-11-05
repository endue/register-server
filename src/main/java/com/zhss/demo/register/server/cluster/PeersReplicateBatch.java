package com.zhss.demo.register.server.cluster;

import com.zhss.demo.register.server.web.AbstractRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:
 * @Description: 集群同步batch
 * @Date: 2020/11/5 22:31
 * @Version: 1.0
 */
public class PeersReplicateBatch {

    private List<AbstractRequest> requests = new ArrayList<>();

    public void add(AbstractRequest request) {
        this.requests.add(request);
    }

    public List<AbstractRequest> getRequests() {
        return requests;
    }
    public void setRequests(List<AbstractRequest> requests) {
        this.requests = requests;
    }
}
