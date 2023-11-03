package com.socia1ca3t.timepillbackup.util;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

public class RestTemplateUtil {


    public static RestTemplate getResttemplate() throws NoSuchAlgorithmException {

        return new RestTemplate(getHTTPClientFactory());
    }

    public static HttpComponentsClientHttpRequestFactory getHTTPClientFactory() throws NoSuchAlgorithmException {


        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        CloseableHttpClient httpClient = httpClientBuilder
                .setConnectionManager(poolingConnectionManager())
                .evictExpiredConnections()
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);

    }


    private static HttpClientConnectionManager poolingConnectionManager() throws NoSuchAlgorithmException {


        final SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder
                .create()
                .setSslContext(SSLContext.getDefault())
                .build();

        return PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();
    }


}
