package com.sohu.mp.sharingplan.aop;

import com.sohu.mp.sharingplan.annotation.WriteDatasource;
import com.sohu.mp.sharingplan.datasource.DynamicDatasource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author lvjinwang
 * @date 2018/7/10
 */
@Component
@Aspect
public class DataSourceAop {

    private static final Pattern READ_PATTERN = Pattern.compile("^(query|get|select|by|is|count|list)");

    @Before("execution(* com.sohu.mp.sharingplan.dao.accounts..*.*(..))")
    public void setDataSource(JoinPoint point) {
        //被注解的用写库
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();

        if (targetMethod.getAnnotation(WriteDatasource.class) != null) {
            DynamicDatasource.setWrite();
            return;
        }

        simpleSetDataSource(point);
    }

    private void simpleSetDataSource(JoinPoint point) {
        String methodName = point.getSignature().getName();
        if (READ_PATTERN.matcher(methodName).find()) {
            DynamicDatasource.setRead();
        } else {
            DynamicDatasource.setWrite();
        }
    }
}
