package com.youming.youche.cloud.provider.configure.cmb;



import com.youming.youche.cloud.provider.service.cmb.OpenApiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JytfzConfiguration {

    @Bean
    public OpenApiService getOpenApiService(){
        return new OpenApiService(getJytfzProperties());
    }

    @Bean
    public JytfzProperties getJytfzProperties(){
        return new JytfzProperties();
    }
}
