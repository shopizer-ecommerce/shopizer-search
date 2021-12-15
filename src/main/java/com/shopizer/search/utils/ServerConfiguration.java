package com.shopizer.search.utils;

public class ServerConfiguration {


  /** Configuration information **/
  private String mode;
  private String protocole;
  private String clusterHost;
  private int clusterPort;
  private String clusterName;
  private String proxyUser;
  private String proxyPassword;
  private Boolean securityEnabled;
  private String elasticSearchUser;
  private String elasticSearchPassword;

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

  public String getProxyUser() {
    return proxyUser;
  }

  public void setProxyUser(String proxyUser) {
    this.proxyUser = proxyUser;
  }

  public String getProxyPassword() {
    return proxyPassword;
  }

  public void setProxyPassword(String proxyPassword) {
    this.proxyPassword = proxyPassword;
  }

  public Boolean getSecurityEnabled() {
    return securityEnabled;
  }

  public void setSecurityEnabled(Boolean securityEnabled) {
    this.securityEnabled = securityEnabled;
  }

  public String getElasticSearchUser() {
    return elasticSearchUser;
  }

  public void setElasticSearchUser(String elasticSearchUser) {
    this.elasticSearchUser = elasticSearchUser;
  }

  public String getElasticSearchPassword() {
    return elasticSearchPassword;
  }

  public void setElasticSearchPassword(String elasticSearchPassword) {
    this.elasticSearchPassword = elasticSearchPassword;
  }

  public String getProtocole() {
    return protocole;
  }

  public void setProtocole(String protocole) {
    this.protocole = protocole;
  }


}
