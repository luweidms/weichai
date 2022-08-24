package com.youming.youche.capital.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages =  { "com.youming.youche.commons", "com.youming.youche.capital"})
public class BusinessCapitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessCapitalApplication.class,args);
    }
}
