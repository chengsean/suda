package org.suda;

import org.suda.handler.MethodArgumentHandler;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMapMethodArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 该对象作用于{@link PathVariable}注解{@link Map}参数的安全检查
 * @author chengshaozhuang
 * @dateTime 2024-07-29 16:48
 */
public class SecurityPathVariableMapMethodArgumentResolver extends PathVariableMapMethodArgumentResolver {

    private final MethodArgumentHandler stringMethodArgumentHandler;

    public SecurityPathVariableMapMethodArgumentResolver(MethodArgumentHandler stringMethodArgumentHandler) {
        this.stringMethodArgumentHandler = stringMethodArgumentHandler;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object arg = super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        return stringMethodArgumentHandler.securityChecks(arg, servletRequest, parameter);
    }
}
