package com.sohu.mp.sharingplan.interceptor;

import com.sohu.mp.sharingplan.annotation.MonitorServerError;
import com.sohu.mp.sharingplan.service.CommonApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final String BONUS = "bonus";
    private static final String BASE = "base";

    @Resource
    private CommonApiService commonApiService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod) {
            String title;
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(MonitorServerError.class)) {
                if(response.getStatus()==500){
                    if (handlerMethod.getMethod().getName().contains("base")){
                        title = "【"+BASE+"处罚操作报错信息】";
                    }else {
                        title = "【"+BONUS+"处罚操作报错信息】";
                    }
                    commonApiService.sendEmail(title,"服务器错误", ERROR_EMAIL);
                }
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}