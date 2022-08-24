package com.youming.youche.order.provider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@MapperScan(basePackages = "com.youming.youche.order.provider.mapper")
@ComponentScan(basePackages = { "com.youming.youche.commons", "com.youming.youche.order.provider" })
@EntityScan(basePackages = "com.youming.youche.order.domain")
@EnableTransactionManagement
public class ProviderOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderOrderApplication.class, args);
    }

}
