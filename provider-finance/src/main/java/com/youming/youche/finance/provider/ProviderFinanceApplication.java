package com.youming.youche.finance.provider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@MapperScan(basePackages = "com.youming.youche.finance.provider.mapper")
@ComponentScan(basePackages = { "com.youming.youche.commons", "com.youming.youche.finance.provider" })
public class ProviderFinanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProviderFinanceApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		// 配置默认的加密方式
		return new BCryptPasswordEncoder();
	}

}
