package com.youming.youche.market.provider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@MapperScan(basePackages = "com.youming.youche.market.provider.mapper")
@ComponentScan(basePackages = { "com.youming.youche.commons", "com.youming.youche.market.provider" ,"com.youming.youche.record.common"})
@EnableAsync
@EntityScan(basePackages = "com.youming.youche.market.domain")
public class ProviderMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderMarketApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // 配置默认的加密方式
        return new BCryptPasswordEncoder();
    }
}
