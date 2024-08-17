package com.socia1ca3t;

import com.socia1ca3t.timepillbackup.config.CurrentUserAccessResolver;
import com.socia1ca3t.timepillbackup.core.ImgPathProducer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

        argumentResolvers.add(new CurrentUserAccessResolver());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 动态添加 FILE_BASE_PATH
        String fileBasePath = ImgPathProducer.FILE_BASE_PATH;
        registry.addResourceHandler("/**")
                .addResourceLocations("file:" + fileBasePath)
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true); // 启用资源链
    }
}
