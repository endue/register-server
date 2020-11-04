package com.zhss.demo.register.server.web;

import com.zhss.demo.register.server.core.ServiceInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author:
 * @Description: 完整的服务实例的信息
 * @Date: 2020/10/31 15:37
 * @Version: 1.0
 */
public class Applications {

    private Map<String, Map<String, ServiceInstance>> registry = new HashMap<String, Map<String, ServiceInstance>>();

    public Applications() {

    }

    public Applications(Map<String, Map<String, ServiceInstance>> registry) {
        this.registry = registry;
    }

    public Map<String, Map<String, ServiceInstance>> getRegistry() {
        return registry;
    }
    public void setRegistry(Map<String, Map<String, ServiceInstance>> registry) {
        this.registry = registry;
    }
}
