package com.lingyu.common.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Server {
	private int worldId;
	private String worldName;
	private String name;
	private int id;
	private int type;
	private int subType;
	private String innerIp;
	private String externalIp;
	private int innerPort;
	private int tcpPort;
	private int webPort;
	private int followerId;
	private String key;// 服务器的公钥
	private int status;
	private String pid;
	private Cache cache;
	private String serverVersion;
	private String dataVersion;
	private int num;
	

	public Server() {

	}

	// public Server(int id, String name, int type, String ip, int port) {
	// this(id, name, type, 0, ip, port);
	// }

	// public Server(int id, String name, int type, int subType, String innerIp,
	// int innerPort, int tcpPort, int webPort) {
	// this.id = id;
	// this.name = name;
	// this.type = type;
	// this.subType = subType;
	// this.innerIp = innerIp;
	// this.innerPort = innerPort;
	// this.tcpPort = tcpPort;
	// this.webPort = webPort;
	// }

	public int getWorldId() {
		return worldId;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public String getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}

	public String getDataVersion() {
		return dataVersion;
	}

	public void setDataVersion(String dataVersion) {
		this.dataVersion = dataVersion;
	}

	public int getFollowerId() {
		return followerId;
	}

	public void setFollowerId(int followerId) {
		this.followerId = followerId;
	}

	public void setWorldId(int worldId) {
		this.worldId = worldId;
	}

	public String getWorldName() {
		return worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public int getWebPort() {
		return webPort;
	}

	public void setWebPort(int webPort) {
		this.webPort = webPort;
	}

	public int getTcpPort() {
		return tcpPort;
	}

	public void setTcpPort(int tcpPort) {
		this.tcpPort = tcpPort;
	}

	public int getSubType() {
		return subType;
	}

	public void setSubType(int subType) {
		this.subType = subType;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getInnerPort() {
		return innerPort;
	}

	public void setInnerPort(int innerPort) {
		this.innerPort = innerPort;
	}

	public String getInnerIp() {
		return innerIp;
	}

	public void setInnerIp(String innerIp) {
		this.innerIp = innerIp;
	}

	public String getExternalIp() {
		return externalIp;
	}

	public void setExternalIp(String externalIp) {
		this.externalIp = externalIp;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
