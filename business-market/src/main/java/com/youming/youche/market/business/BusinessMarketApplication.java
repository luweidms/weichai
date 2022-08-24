package com.youming.youche.market.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {"com.youming.youche.commons", "com.youming.youche.market.business","com.youming.youche.market.aspect","com.youming.youche.market.utils"})
public class BusinessMarketApplication {
    public static void main(String[] args) {
        SpringApplication.run(BusinessMarketApplication.class, args);
    }

}
