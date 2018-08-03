package com.sohu.mp.sharingplan.util;

import com.sohu.mp.common.exception.InvalidParameterException;
import com.sohu.mp.common.util.CodecUtil;
import com.sohu.mp.sharingplan.enums.exception.MulctNoAuthException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 校验参数 从request获取参数等
 *
 * @author lvjinwang
 * @date 2018-08-03
 */
public class ParamCheckUtil {

    private ParamCheckUtil() {
    }

    public static void checkOperatorAuth(String sign, String reason, String operator) {
        if (StringUtils.isAnyBlank(reason, reason, operator)) {
            throw new InvalidParameterException("reason is empty");
        }
        if (!sign.equals(CodecUtil.hmacSha1(operator))) {
            throw MulctNoAuthException.INSTANCE;
        }
    }

    public static Integer getIntParam(HttpServletRequest request, String paramName) {
        return convertStr2Int(getParam(request, paramName));
    }

    public static Set<Integer> getSetParam(HttpServletRequest request, String paramName) {
        return convertStr2Set(getParam(request, paramName));
    }

    public static String getParam(HttpServletRequest request, String paramName) {
        String param;

        // get from path
        param = getParamFromPath(request, paramName);
        if (param != null) {
            return param;
        }
        // get from param
        param = getParamFromParam(request, paramName);
        if (param != null) {
            return param;
        }
        return null;
    }

    private static String getParamFromPath(HttpServletRequest request, String paramName) {
        Object attribute = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        @SuppressWarnings("unchecked")
        Map<String, String> attributeMap = (Map<String, String>) attribute;
        return attributeMap.get(paramName);
    }

    private static String getParamFromParam(HttpServletRequest request, String paramName) {
        return request.getParameter(paramName);
    }

    private static Integer convertStr2Int(String param) {
        if (param == null) {
            return null;
        }
        try {
            return Integer.valueOf(param);
        } catch (Exception e) {
            throw new InvalidParameterException("无效的参数:" + param);
        }
    }

    private static Set<Integer> convertStr2Set(String param) {
        if (param == null) {
            return Collections.emptySet();
        }
        try {
            Set<Integer> ids = new HashSet<>();
            String[] array = param.split(",");
            for (String idStr : array) {
                ids.add(Integer.valueOf(idStr));
            }
            return ids;
        } catch (Exception e) {
            throw new InvalidParameterException("无效的参数:" + param);
        }
    }

}
