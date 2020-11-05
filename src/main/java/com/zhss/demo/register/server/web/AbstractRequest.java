package com.zhss.demo.register.server.web;

/**
 * @Author:
 * @Description: 请求接口
 * @Date: 2020/11/4 22:20
 * @Version: 1.0
 */
public abstract class AbstractRequest {

    public static final Integer REGISTER_REQUEST = 1;
    public static final Integer CANCEL_REQUEST = 2;
    public static final Integer HEARTBEAT_REQUEST = 3;

    /**
     * 服务名称
     */
    protected String serviceName;
    /**
     * 服务实例id
     */
    protected String serviceInstanceId;
    /**
     * 请求类型
     */
    protected Integer type;

    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    public String getServiceInstanceId() {
        return serviceInstanceId;
    }
    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "HeartbeatRequest [serviceName=" + serviceName + ", serviceInstanceId=" + serviceInstanceId + "]";
    }
}
