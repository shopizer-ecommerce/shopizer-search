package com.shopizer.search.utils;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * Singleton
 * 
 * @author Carl Samson
 * 
 *         Back to elasticsearch high level REST client
 *
 */
public class SearchClient {

  private static Logger log = Logger.getLogger(SearchClient.class);

  private RestHighLevelClient client = null;
  private boolean init = false;
  private ServerConfiguration serverConfiguration = null;

  private String authenticationHeader = null;

  public ServerConfiguration getServerConfiguration() {
    return serverConfiguration;
  }

  public void setServerConfiguration(ServerConfiguration serverConfiguration) {
    this.serverConfiguration = serverConfiguration;
  }

  public RestHighLevelClient getClient() {
    if (!init) {
      initClient();
    }
    return client;
  }

  public SearchClient() {}


  private synchronized void initClient() {

    if (client == null) {

      try {

        // host
        // port
        // proxy settings
        // security


        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials(getServerConfiguration().getElasticSearchUser(),
                getServerConfiguration().getElasticSearchPassword()));
        
        String clusterHost = getServerConfiguration().getClusterHost();
        int clusterPort = getServerConfiguration().getClusterPort();
        String protocole = getServerConfiguration().getProtocole();
        
        log.debug("Elastic search client connecting to "+ protocole + "://" + clusterHost + ":" + clusterPort);


        RestClientBuilder builder =
            RestClient.builder(new HttpHost(clusterHost,clusterPort,protocole));

        if (getServerConfiguration().getSecurityEnabled() != null
            && getServerConfiguration().getSecurityEnabled().booleanValue()) {
          builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(
                HttpAsyncClientBuilder httpClientBuilder) {
              return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
          });
        }

        client = new RestHighLevelClient(builder);

        this.init = true;

        log.debug("****** ES client ready ********");

      } catch (Exception e) {
        e.printStackTrace();
      }

    }

  }

  public String getAuthenticationHeader() {
    return authenticationHeader;
  }

  public void setAuthenticationHeader(String authenticationHeader) {
    this.authenticationHeader = authenticationHeader;
  }

}
