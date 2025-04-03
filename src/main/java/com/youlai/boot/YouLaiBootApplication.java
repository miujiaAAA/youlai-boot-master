package com.youlai.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 应用启动类
 *
 * @author Ray.Hao
 * @since 0.0.1
 */
@SpringBootApplication
@ConfigurationPropertiesScan // 开启配置属性绑定
// @EnableScheduling // 开启定时任务
public class YouLaiBootApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(YouLaiBootApplication.class); // 替换为你的主应用类名
    }
    public static void main(String[] args) {
        SpringApplication.run(YouLaiBootApplication.class, args);
    }

}
