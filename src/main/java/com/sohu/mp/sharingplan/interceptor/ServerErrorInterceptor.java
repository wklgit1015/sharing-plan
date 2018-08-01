package com.sohu.mp.sharingplan.interceptor;

import com.sohu.mp.sharingplan.annotation.MonitorServerError;
import com.sohu.mp.sharingplan.service.CommonApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ServerErrorInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ServerErrorInterceptor.class);
    private static final String ERROR_EMAIL = "jinwanglv213697@sohu-inc.com";

    @Resource
    private CommonApiService commonApiService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(MonitorServerError.class)
                    && response.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                String methodName = handlerMethod.getMethod().getName();
                logger.error("[monitor server error]: method={}", methodName);
                commonApiService.sendEmail("【分成计划处罚操作报错信息】", methodName, ERROR_EMAIL);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}