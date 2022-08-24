package com.youming.youche.table.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = {"com.youming.youche.commons","com.youming.youche.table"})
public class BusinessTableApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessTableApplication.class, args);
    }

}
