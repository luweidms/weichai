package com.youming.youche.system.business.configure;

import com.youming.youche.commons.oauth.AuthExceptionEntryPoint;
import com.youming.youche.commons.oauth.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.annotation.Resource;

/**
 * 资源服务器
 * <p>
 * Description:
 * </p>
 *
 * @see com.youming.youche.system
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class ProfileResourceServerConfiguration extends ResourceServerConfigurerAdapter {

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}




	@Override
	public void configure(HttpSecurity http) throws Exception {

		// 开放
		http.exceptionHandling().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and().authorizeRequests().antMatchers(
						"/mycenter/user/sendVerifyCodeByResetPwd/**",
						"/mycenter/user/checkVerifyCodeByResetPwd",
						"/mycenter/user/resetPwd",
						"/wechat/user/getCode/*",
						"/wechat/user/bind"
		).permitAll()
				.and().authorizeRequests().antMatchers("/**").hasAuthority("USER")
//				.and().exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
		;
		// http.authorizeRequests().anyRequest().authenticated()
		// .and().requestMatchers().antMatchers("")
		;

	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		// 配置资源 ID
		resources.resourceId("backend-resources")
				.authenticationEntryPoint(new AuthExceptionEntryPoint())
//		.accessDeniedHandler(new CustomAccessDeniedHandler())
		;
	}

}
