package com.sohu.mp.sharingplan.resolver;

import com.sohu.mp.common.exception.UserNotFoundException;
import com.sohu.mp.sharingplan.annotation.Passport;
import com.sohu.mp.sharingplan.model.MpProfile;
import com.sohu.mp.sharingplan.service.MpProfileService;
import com.sohu.mp.sharingplan.util.ParamCheckUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author lvjinwang
 * @date 2018/8/3
 */
@Component
public class MpProfileResolver implements HandlerMethodArgumentResolver {

    @Resource
    private MpProfileService mpProfileService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(Passport.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String passport = ParamCheckUtil.getParam(httpServletRequest, "passport");
        if (StringUtils.isBlank(passport)) {
            throw new ServletRequestBindingException("缺少passport参数");
        }
        MpProfile mpProfile = mpProfileService.getByPassport(passport);
        if (mpProfile == null) {
            throw UserNotFoundException.INSTANCE;
        }
        return mpProfile;
    }


}
