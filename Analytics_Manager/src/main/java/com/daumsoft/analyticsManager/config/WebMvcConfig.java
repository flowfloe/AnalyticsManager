package com.daumsoft.analyticsManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.daumsoft.analyticsManager.common.service.InterceptorService;

@SuppressWarnings("deprecation")
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter{
	
	@Bean
	public InterceptorService interceptor() {
	    return new InterceptorService();
	}
	
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor())
        		.addPathPatterns("/admin/**")
                .addPathPatterns("/*");
    }
}
