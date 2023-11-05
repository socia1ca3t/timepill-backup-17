package com.socia1ca3t;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@ConfigurationPropertiesScan
@SpringBootApplication
public class MainApplication {


    public static void main(String[] args) {

        SpringApplication.run(MainApplication.class, args);
    }
}
