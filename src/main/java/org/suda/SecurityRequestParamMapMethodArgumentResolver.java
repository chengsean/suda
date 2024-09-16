package org.suda;

import org.springframework.util.MultiValueMap;
import org.suda.handler.MethodArgumentHandler;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 该对象作用于{@link RequestParam} 注解{@link Map}参数的安全检查
 * 暂不支持{@link MultiValueMap}(2024.09.16)
 * @author chengshaozhuang
 * @dateTime 2024-08-16 11:24
 */
public class SecurityRequestParamMapMethodArgumentResolver extends RequestParamMapMethodArgumentResolver {


    private final MethodArgumentHandler stringMethodArgumentHandler;

    public SecurityRequestParamMapMethodArgumentResolver(MethodArgumentHandler stringMethodArgumentHandler) {
        this.stringMethodArgumentHandler = stringMethodArgumentHandler;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object arg = super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (servletRequest != null) {
            return stringMethodArgumentHandler.securityChecks(arg, servletRequest, parameter);
        }
        return arg;
    }
}