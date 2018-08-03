package com.sohu.mp.sharingplan.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * mpProfile 映射
 * 请求中含有 passport 时，用此注解直接获取 mpProfile 对象
 *
 * @author lvjinwang
 * @date 2018/8/3
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Passport {
}
