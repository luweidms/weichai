package com.youming.youche.system.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages =  { "com.youming.youche.commons", "com.youming.youche.system.business"})
@EnableTransactionManagement
public class BusinessSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusinessSystemApplication.class, args);
	}

}
