package com.shopizer.search.utils;

public class ServerConfiguration {
	

	/** Configuration information **/
	private String clusterName;
	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getClusterHost() {
		return clusterHost;
	}

	public void setClusterHost(String clusterHost) {
		this.clusterHost = clusterHost;
	}

	public int getClusterPort() {
		return clusterPort;
	}

	public void setClusterPort(int clusterPort) {
		this.clusterPort = clusterPort;
	}

	private String mode;
	private String clusterHost;
	private int clusterPort;


}
