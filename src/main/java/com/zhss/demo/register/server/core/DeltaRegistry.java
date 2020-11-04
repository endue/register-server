package com.zhss.demo.register.server.core;

import com.zhss.demo.register.server.ServiceRegistry;

import java.util.LinkedList;
import java.util.Queue;


/**
 * 增量注册表
 */
public class DeltaRegistry {

	private Queue<ServiceRegistry.RecentlyChangedServiceInstance> recentlyChangedQueue;
	private Long serviceInstanceTotalCount;
	
	public DeltaRegistry(Queue<ServiceRegistry.RecentlyChangedServiceInstance> recentlyChangedQueue,
						 Long serviceInstanceTotalCount) {
		this.recentlyChangedQueue = recentlyChangedQueue;
		this.serviceInstanceTotalCount = serviceInstanceTotalCount;
	}
	
	public Queue<ServiceRegistry.RecentlyChangedServiceInstance> getRecentlyChangedQueue() {
		return recentlyChangedQueue;
	}
	public void setRecentlyChangedQueue(LinkedList<ServiceRegistry.RecentlyChangedServiceInstance> recentlyChangedQueue) {
		this.recentlyChangedQueue = recentlyChangedQueue;
	}
	public Long getServiceInstanceTotalCount() {
		return serviceInstanceTotalCount;
	}
	public void setServiceInstanceTotalCount(Long serviceInstanceTotalCount) {
		this.serviceInstanceTotalCount = serviceInstanceTotalCount;
	}
	
}