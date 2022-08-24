package com.youming.youche.cloud.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class BusinessCloudApplication {
    public static void main(String[] args) {
        SpringApplication.run(BusinessCloudApplication.class,args);
    }
}
