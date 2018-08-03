package com.sohu.mp.sharingplan.config;

import com.sohu.mp.sharingplan.interceptor.ServerErrorInterceptor;
import com.sohu.mp.sharingplan.resolver.MpProfileResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;
import java.util.List;

@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter {

    @Resource
    private MpProfileResolver mpProfileResolver;

    @Resource
    private ServerErrorInterceptor serverErrorInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(serverErrorInterceptor);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(mpProfileResolver);
    }
}
