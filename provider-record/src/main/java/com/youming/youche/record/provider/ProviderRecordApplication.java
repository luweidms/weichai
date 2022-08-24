package com.youming.youche.record.provider;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication

@ComponentScan(basePackages = {"com.youming.youche.commons","com.youming.youche.record.provider"})
@MapperScan(basePackages = "com.youming.youche.record.provider.mapper")
@EntityScan(basePackages = "com.youming.youche.domain")
@EnableAsync
@EnableTransactionManagement
public class ProviderRecordApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderRecordApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // 配置默认的加密方式
        return new BCryptPasswordEncoder();
    }

}
