package com.zhss.demo.register.server;

import com.zhss.demo.register.server.core.DeltaRegistry;
import com.zhss.demo.register.server.core.ServiceInstance;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 注册表
 */
public class ServiceRegistry {

	public static final Long RECENTLY_CHANGED_ITEM_CHECK_INTERVAL = 3000L;
	public static final Long RECENTLY_CHANGED_ITEM_EXPIRED = 3 * 60 * 1000L;

	// 注册表是单例
	private static ServiceRegistry instance = new ServiceRegistry();
	
	private ServiceRegistry() {
		RecentlyChangedQueueMonitor recentlyChangedQueueMonitor = new RecentlyChangedQueueMonitor();
		recentlyChangedQueueMonitor.setDaemon(true);
		recentlyChangedQueueMonitor.start();
	}

	public static ServiceRegistry getInstance() {
		return instance;
	}
	
	/**
	 * 核心的内存数据结构：注册表
	 * Map：key是服务名称，value是这个服务的所有的服务实例
	 */
	private Map<String, Map<String, ServiceInstance>> registry = new ConcurrentHashMap<>();

	// 最近变更的服务实例的队列
	private Queue<RecentlyChangedServiceInstance> recentlyChangedQueue = new ConcurrentLinkedQueue<>();

	// 注册表读写锁
	private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
	private ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
	
	/**
	 * 服务注册
	 * @param serviceInstance 服务实例
	 */
	public void register(ServiceInstance serviceInstance) {

		try {
			this.writeLock();

			Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceInstance.getServiceName());
			if(serviceInstanceMap == null) {
				serviceInstanceMap = new HashMap<>();
				registry.put(serviceInstance.getServiceName(), serviceInstanceMap);
			}
			serviceInstanceMap.put(serviceInstance.getServiceInstanceId(), serviceInstance);
			System.out.println("服务实例【" + serviceInstance + "】，完成注册......");
			System.out.println("注册表：" + registry);

			// 记录最近变更队列
			RecentlyChangedServiceInstance recentlyChangedItem = new RecentlyChangedServiceInstance(
					serviceInstance,
					System.currentTimeMillis(),
					ServiceInstanceOperation.REGISTER);
			recentlyChangedQueue.offer(recentlyChangedItem);
		}finally {
			this.writeUnlock();
		}
	}
	
	/**
	 * 获取服务实例
	 * @param serviceName 服务名称
	 * @param serviceInstanceId 服务实例id
	 * @return 服务实例
	 */
	public ServiceInstance getServiceInstance(String serviceName, String serviceInstanceId) {
		Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceName);
		return serviceInstanceMap.get(serviceInstanceId);
	}
	
	/**
	 * 获取整个注册表
	 * @return
	 */
	public Map<String, Map<String, ServiceInstance>> getRegistry() {
		return registry;
	}

	/**
	 * 获取最近有变化的注册表
	 * @return
	 */
	public DeltaRegistry getRecentlyChangedQueue() {
		Long totalCount = 0L;
		for(Map<String, ServiceInstance> serviceInstanceMap : registry.values()) {
			totalCount += serviceInstanceMap.size();
		}
		DeltaRegistry deltaRegistry = new DeltaRegistry(recentlyChangedQueue, totalCount);
		return deltaRegistry;
	}

	/**
	 * 从注册表删除一个服务实例
	 * @param serviceName
	 * @param serviceInstanceId
	 */
	public void remove(String serviceName, String serviceInstanceId) {
		try {
			this.writeLock();

			System.out.println("服务实例【" + serviceInstanceId + "】，从注册表中进行摘除");
			Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceName);
			ServiceInstance serviceInstance = serviceInstanceMap.get(serviceInstanceId);

			// 将服务实例变更信息放入队列中
			RecentlyChangedServiceInstance recentlyChangedItem = new RecentlyChangedServiceInstance(
					serviceInstance,
					System.currentTimeMillis(),
					ServiceInstanceOperation.REMOVE);
			recentlyChangedQueue.offer(recentlyChangedItem);

			// 从注册中心删除服务
			serviceInstanceMap.remove(serviceInstanceId);
		}finally {
			this.writeUnlock();
		}
	}

	/**
	 * 获取最近有变化的注册表
	 * @return
	 */
	public DeltaRegistry getDeltaRegistry() {
		Long totalCount = 0L;
		for(Map<String, ServiceInstance> serviceInstanceMap : registry.values()) {
			totalCount += serviceInstanceMap.size();
		}
		DeltaRegistry deltaRegistry = new DeltaRegistry(recentlyChangedQueue, totalCount);
		return deltaRegistry;
	}

	/**
	 * 最近变化的服务实例
	 */
	public class RecentlyChangedServiceInstance{
		/**
		 * 服务实例
		 */
		ServiceInstance serviceInstance;
		/**
		 * 发生变更的时间戳
		 */
		Long changedTimestamp;
		/**
		 * 变更操作
		 */
		String serviceInstanceOperation;

		public RecentlyChangedServiceInstance(ServiceInstance serviceInstance, Long changedTimestamp, String serviceInstanceOperation) {
			this.serviceInstance = serviceInstance;
			this.changedTimestamp = changedTimestamp;
			this.serviceInstanceOperation = serviceInstanceOperation;
		}
	}

	/**
	 * 服务实例操作
	 */
	class ServiceInstanceOperation {

		// 注册
		public static final String REGISTER = "register";
		// 删除
		public static final String REMOVE = "REMOVE";
	}

	/**
	 * 最近变更队列的监控线程
	 */
	class RecentlyChangedQueueMonitor extends Thread{
		@Override
		public void run() {
			while (true){
				try {
					synchronized(instance) {
						RecentlyChangedServiceInstance recentlyChangedItem = null;
						Long currentTimestamp = System.currentTimeMillis();

						while((recentlyChangedItem = recentlyChangedQueue.peek()) != null) {
							// 判断如果一个服务实例变更信息已经在队列里存在超过3分钟了
							// 就从队列中移除
							if(currentTimestamp - recentlyChangedItem.changedTimestamp
									> RECENTLY_CHANGED_ITEM_EXPIRED) {
								recentlyChangedQueue.poll();
							}
						}
					}
					Thread.sleep(RECENTLY_CHANGED_ITEM_CHECK_INTERVAL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}


	public void readLock() {
		this.readLock.lock();
	}

	public void readUnlock() {
		this.readLock.unlock();
	}

	public void writeLock() {
		this.writeLock.lock();
	}

	public void writeUnlock() {
		this.writeLock.unlock();
	}
}
