package com.zhss.demo.register.server.core;

import com.zhss.demo.register.server.web.Applications;
import com.zhss.demo.register.server.ServiceRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author:
 * @Description: 服务注册表的缓存
 * @Date: 2020/10/31 15:46
 * @Version: 1.0
 */
public class ServiceRegistryCache {

    /**
     * 单例
     */
    private static final ServiceRegistryCache instance = new ServiceRegistryCache();

    public static ServiceRegistryCache getInstance() {
        return instance;
    }
    // 缓存数据同步间隔
    private static final Long CACHE_MAP_SYNC_INTERVAL = 30 * 1000L;

    public static class CacheKey {
        // 全量注册表缓存key
        public static final String FULL_SERVICE_REGISTRY = "full_service_registry";
        // 增量注册表缓存key
        public static final String DELTA_SERVICE_REGISTRY = "delta_service_registry";
    }
    // 注册表
    private ServiceRegistry registry = ServiceRegistry.getInstance();
    // 只读缓存
    private Map<String, Object> readOnlyMap = new HashMap<>();
    // 读写缓存
    private Map<String, Object> readWriteMap = new HashMap<>();
    // cache map同步后台线程
    private CacheMapSyncDaemon cacheMapSyncDaemon;
    // 内部锁
    private Object lock = new Object();
    // 对readOnlyMap的读写锁
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
    private ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();

    public ServiceRegistryCache() {
        this.cacheMapSyncDaemon = new CacheMapSyncDaemon();
        cacheMapSyncDaemon.setDaemon(true);
        cacheMapSyncDaemon.start();
    }

    /**
     * 删除对应的缓存
     */
    public void invalidate(){
        synchronized (lock){
            readWriteMap.remove(CacheKey.FULL_SERVICE_REGISTRY);
            readWriteMap.remove(CacheKey.DELTA_SERVICE_REGISTRY);
        }
    }

    public Object get(String cacheKey){
        Object cacheValue;
        try {
            this.readLock.lock();

            cacheValue = readOnlyMap.get(cacheKey);
            if(cacheValue == null){
                synchronized (lock){
                    if((cacheValue = readOnlyMap.get(cacheKey)) == null){
                        if((cacheValue = readWriteMap.get(cacheKey)) == null){
                            cacheValue = getCacheValue(cacheKey);
                            readWriteMap.put(cacheKey,cacheValue);
                        }
                        readOnlyMap.put(cacheKey,cacheValue);
                    };
                }
            }
        }finally {
            this.readLock.unlock();
        }
        return cacheValue;
    }

    public Object getCacheValue(String cacheKey) {

        try {
            registry.readLock();

            if(CacheKey.FULL_SERVICE_REGISTRY.equals(cacheKey)) {
                return new Applications(registry.getRegistry());
            } else if(CacheKey.DELTA_SERVICE_REGISTRY.equals(cacheKey)) {
                return registry.getDeltaRegistry();
            }
        }finally {
            registry.readUnlock();
        }
        return null;
    }

    class CacheMapSyncDaemon extends Thread{
        @Override
        public void run() {
            while (true){
                try {
                    synchronized (lock){
                        if(readWriteMap.get(CacheKey.FULL_SERVICE_REGISTRY) == null) {
                            try {
                                writeLock.lock();
                                readOnlyMap.put(CacheKey.FULL_SERVICE_REGISTRY, null);
                            } finally {
                                writeLock.unlock();
                            }
                        }
                        if(readWriteMap.get(CacheKey.DELTA_SERVICE_REGISTRY) == null) {
                            try {
                                writeLock.lock();
                                readOnlyMap.put(CacheKey.DELTA_SERVICE_REGISTRY, null);
                            } finally {
                                writeLock.unlock();
                            }
                        }
                    }

                    Thread.sleep(CACHE_MAP_SYNC_INTERVAL);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }
}
