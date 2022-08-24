package com.youming.youche.table.provider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan(basePackages = "com.youming.youche.table.provider.mapper")
@ComponentScan(basePackages = { "com.youming.youche.commons", "com.youming.youche.table.provider" })
@EnableAsync
@EntityScan(basePackages = "com.youming.youche.table.domain")
public class ProviderTableApplication {


    public static void main(String[] args) {
        SpringApplication.run(ProviderTableApplication.class, args);
    }
}
