package com.sohu.mp.sharingplan.config;

import com.sohu.mp.sharingplan.interceptor.ServerErrorInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter {

    @Resource
    private ServerErrorInterceptor serverErrorInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(serverErrorInterceptor);
    }
}
