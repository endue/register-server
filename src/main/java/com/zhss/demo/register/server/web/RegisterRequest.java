package com.zhss.demo.register.server.web;

/**
 * 注册请求
 *
 */
public class RegisterRequest extends AbstractRequest{

	/**
	 * 服务所在机器的ip地址
	 */
	private String ip;
	/**
	 * 服务所在机器的主机名
	 */
	private String hostname;
	/**
	 * 服务监听着哪个端口号
	 */
	private int port;
	

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "RegisterRequest [serviceName=" + serviceName + ", ip=" + ip + ", hostname=" + hostname + ", port="
				+ port + ", serviceInstanceId=" + serviceInstanceId + "]";
	}
	
}
