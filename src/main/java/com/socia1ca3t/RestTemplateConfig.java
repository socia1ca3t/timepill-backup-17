package com.socia1ca3t;


import com.socia1ca3t.timepillbackup.util.RestTemplateUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class RestTemplateConfig {

    @Bean
    public RestTemplate getRestTemplate() throws Exception {

        return RestTemplateUtil.getResttemplate();
    }

}
