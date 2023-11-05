package com.socia1ca3t;


import com.socia1ca3t.timepillbackup.util.HttpClientUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class RestTemplateConfig {

    @Bean
    public RestTemplate getHTTPSRestTemplate() {


        return new RestTemplate(HttpClientUtil.getDefaultHTTPSClientFactory());
    }

}
