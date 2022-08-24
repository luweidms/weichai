package com.youming.youche.cloud.provider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication

@MapperScan(basePackages = "com.youming.youche.cloud.provider.mapper")
@ComponentScan(basePackages = { "com.youming.youche.commons", "com.youming.youche.cloud.provider" })
@EntityScan(basePackages = "com.youming.youche.cloud.domain")
public class ProviderCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderCloudApplication.class,args);
    }
}
