package com.zhss.demo.register.server.core;

import com.zhss.demo.register.server.web.HeartbeatRequest;
import com.zhss.demo.register.server.web.RegisterRequest;
import com.zhss.demo.register.server.web.RegisterServerController;

import java.util.UUID;

/**
 * 代表了服务注册中心
 *
 */
public class ServerRegistey {
	
	public static void main(String[] args) throws Exception {
		RegisterServerController controller = new RegisterServerController();
		
		String serviceInstanceId = UUID.randomUUID().toString().replace("-", "");
		
		// 模拟发起一个服务注册的请求
		RegisterRequest registerRequest = new RegisterRequest();
		registerRequest.setHostname("inventory-service-01");  
		registerRequest.setIp("192.168.31.208");  
		registerRequest.setPort(9000); 
		registerRequest.setServiceInstanceId(serviceInstanceId);    
		registerRequest.setServiceName("inventory-service");  

		controller.register(registerRequest);
		
		// 模拟进行一次心跳，完成续约
		HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
		heartbeatRequest.setServiceName("inventory-service");  
		heartbeatRequest.setServiceInstanceId(serviceInstanceId); 
		
		controller.heartbeat(heartbeatRequest);
		
		// 开启一个后台线程，检测微服务的存活状态
		ServiceAliveMonitor serviceAliveMonitor = new ServiceAliveMonitor();
		serviceAliveMonitor.start();
		
		while(true) {
			Thread.sleep(30 * 1000);  
		}
	}

}
