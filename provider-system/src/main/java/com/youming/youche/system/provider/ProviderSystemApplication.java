package com.youming.youche.system.provider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = "com.youming.youche.system.provider.mapper")
@EntityScan(basePackages = "com.youming.youche.system.domain")
@ComponentScan(basePackages = { "com.youming.youche.commons", "com.youming.youche.system.provider" })
@EnableTransactionManagement
public class ProviderSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProviderSystemApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		// 配置默认的加密方式
		return new BCryptPasswordEncoder();
	}

}
