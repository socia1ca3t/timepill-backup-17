package com.socia1ca3t;

import com.socia1ca3t.timepillbackup.core.ImgPathProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.util.FileSystemUtils;

import java.io.File;


@EnableCaching
@ConfigurationPropertiesScan
@SpringBootApplication
public class MainApplication {


    public static void main(String[] args) {

        SpringApplication.run(MainApplication.class, args);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // JVM退出时，删除临时文件夹
            FileSystemUtils.deleteRecursively(new File(ImgPathProducer.FILE_BASE_PATH));
        }));
    }
}
