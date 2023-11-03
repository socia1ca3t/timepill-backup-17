package com.socia1ca3t;

import com.socia1ca3t.timepillbackup.pojo.dto.DiariesDTO;
import com.socia1ca3t.timepillbackup.pojo.dto.Diary;
import com.socia1ca3t.timepillbackup.pojo.dto.NoteBook;
import com.socia1ca3t.timepillbackup.pojo.dto.UserInfo;
import com.socia1ca3t.timepillbackup.pojo.vo.*;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;


// jackson 序列化以及 thymeleaf 需要使用对象反射，故用此注解提示 Native Image 编译程序
@RegisterReflectionForBinding({DiariesDTO.class, Diary.class, NoteBook.class, UserInfo.class,
        DiariesIndexVO.class, DiariesStatisticData.class, HomePageVO.class, ArrayList.class,
        NotebookStatisticDataVO.class, UserStatisticDataVO.class, NotebooksOfOneYear.class,
        BackupProgressVO.class})
@ImportRuntimeHints(MainApplication.MyRuntimeHints.class)
@EnableRetry
@EnableCaching
@SpringBootApplication
public class MainApplication {


    public static void main(String[] args) {

        // SecurityContextHolder 的默认策略是 请求的线程内可以获取，修改为全局可获取
        System.setProperty(SecurityContextHolder.SYSTEM_PROPERTY, SecurityContextHolder.MODE_GLOBAL);

        SpringApplication.run(MainApplication.class, args);
    }


    static class MyRuntimeHints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {

            // Register method for reflection
            Method method = ReflectionUtils.findMethod(ArrayList.class, "size");
            hints.reflection().registerMethod(method, ExecutableMode.INVOKE);

            // Register resources
            //hints.resources().registerPattern("my-resource.txt");

            // Register serialization
            //hints.serialization().registerType(MySerializableClass.class);

            // Register proxy
            //hints.proxies().registerJdkProxy(MyInterface.class);
        }

    }


}
