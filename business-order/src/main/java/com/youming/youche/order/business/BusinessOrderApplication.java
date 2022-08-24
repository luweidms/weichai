package com.youming.youche.order.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = {"com.youming.youche.commons","com.youming.youche.order"})
@EnableTransactionManagement
public class BusinessOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessOrderApplication.class, args);
    }
}
