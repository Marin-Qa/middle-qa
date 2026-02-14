//package com.example.config;
//
//
//import org.apache.hc.client5.http.config.RequestConfig;
//import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
//import org.apache.hc.client5.http.impl.classic.HttpClients;
//import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
//import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
//import org.apache.hc.core5.http.io.SocketConfig;
//import org.apache.hc.core5.util.Timeout;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.client.*;
//import org.springframework.web.client.RestClient;
//
//@Configuration
//public class RestClientConfig {
//
//    private final DummyJsonProperties dummyJsonProperties;
//
//    public RestClientConfig(DummyJsonProperties dummyJsonProperties) {
//        this.dummyJsonProperties = dummyJsonProperties;
//    }
//
//    @Bean
//    public RestClient externalClient() {
//
//        SocketConfig socketConfig = SocketConfig.custom()
//                .setSoTimeout(Timeout.ofSeconds(60))
//                .build();
//
//        PoolingHttpClientConnectionManager connectionManager =
//                PoolingHttpClientConnectionManagerBuilder.create()
//                        .setMaxConnTotal(100)
//                        .setMaxConnPerRoute(20)
//                        .setDefaultSocketConfig(socketConfig)
//                        .build();
//
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setConnectionRequestTimeout(Timeout.ofSeconds(5))
//                .setResponseTimeout(Timeout.ofSeconds(60))
//                .build();
//
//        CloseableHttpClient httpClient = HttpClients.custom()
//                .setConnectionManager(connectionManager)
//                .setDefaultRequestConfig(requestConfig)
//                .evictExpiredConnections()
//                .build();
//
//        HttpComponentsClientHttpRequestFactory factory =
//                new HttpComponentsClientHttpRequestFactory(httpClient);
//
//        return RestClient.builder()
//                .baseUrl(dummyJsonProperties.getBaseUrl())
//                .requestFactory(factory)
//                .defaultHeaders(httpHeaders ->
//                        dummyJsonProperties.getDefaultHeaders().forEach(httpHeaders::add))
//                .build();
//    }
//}

package com.example.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private final DummyJsonProperties dummyJsonProperties;

    public RestClientConfig(DummyJsonProperties dummyJsonProperties) {
        this.dummyJsonProperties = dummyJsonProperties;
    }

    @Bean
    public RestClient externalClient() {

        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(dummyJsonProperties.getTimeoutSeconds()))
                .build();

        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(20)
                .setDefaultSocketConfig(socketConfig)
                .build();

        var requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(5))
                .setResponseTimeout(Timeout.ofSeconds(dummyJsonProperties.getTimeoutSeconds()))
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictExpiredConnections()
                .build();

        var factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return RestClient.builder()
                .baseUrl(dummyJsonProperties.getBaseUrl())
                .requestFactory(factory)
                .defaultHeaders(headers -> dummyJsonProperties.getDefaultHeaders().forEach(headers::add))
                .build();
    }
}
