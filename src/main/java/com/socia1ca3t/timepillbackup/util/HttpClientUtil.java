package com.socia1ca3t.timepillbackup.util;

import lombok.SneakyThrows;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;

public class HttpClientUtil {


    public static HttpComponentsClientHttpRequestFactory getDefaultHTTPSClientFactory() {

        return getHTTPSClientFactory(null, null);
    }


    public static HttpComponentsClientHttpRequestFactory getHTTPSClientFactory(
                                                            HttpRequestRetryStrategy retryStrategy,
                                                            HttpRequestInterceptor interceptor) {


        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
                                                .setConnectionManager(poolingConnectionManager())
                                                .evictExpiredConnections();

        if (interceptor != null) {
            httpClientBuilder.addRequestInterceptorFirst(interceptor);
        }

        if (retryStrategy != null) {
            httpClientBuilder.setRetryStrategy(retryStrategy);
        }


        return new HttpComponentsClientHttpRequestFactory(httpClientBuilder.build());
    }


    @SneakyThrows
    private static HttpClientConnectionManager poolingConnectionManager() {

        final SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder
                .create()
                .setSslContext(SSLContext.getDefault())
                .build();

        return PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();
    }


    public static HttpRequestInterceptor getBasicAuthForTargetHOSTInterceptor(URI targetURI, String username, String pwd) {

        return new HttpRequestInterceptor() {
            @SneakyThrows
            @Override
            public void process(HttpRequest request, EntityDetails entity, HttpContext context) {

                String requestHost = request.getUri().getHost();
                String scheme = request.getUri().getScheme();

                if (requestHost.equals(targetURI.getHost()) && "https".equals(scheme)) {

                    String encoding = Base64.getEncoder().encodeToString((username + ":" + pwd).getBytes());
                    request.addHeader(new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding));
                }
            }
        };

    }


}
