package com.youming.youche.capital.provider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication

@MapperScan(basePackages = "com.youming.youche.capital.provider.mapper")
@ComponentScan(basePackages = {"com.youming.youche.commons","com.youming.youche.capital.provider"})
@EntityScan(basePackages = "com.youming.youche.capital.domain")
public class ProviderCapitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderCapitalApplication.class,args);
    }
}
