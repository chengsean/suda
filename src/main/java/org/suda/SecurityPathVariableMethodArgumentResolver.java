package org.suda;

import org.suda.handler.MethodArgumentHandler;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;

import javax.servlet.http.HttpServletRequest;


/**
 * 该对象作用于{@link PathVariable} 注解参数的安全检查
 * @author chengshaozhuang
 * @dateTime 2024-07-29 15:11
 */
public class SecurityPathVariableMethodArgumentResolver extends PathVariableMethodArgumentResolver {


    private final MethodArgumentHandler stringMethodArgumentHandler;

    public SecurityPathVariableMethodArgumentResolver(MethodArgumentHandler stringMethodArgumentHandler) {
        this.stringMethodArgumentHandler = stringMethodArgumentHandler;
    }

    @Override
    protected void handleResolvedValue(Object arg, String name, MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest request) {
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        if (servletRequest != null) {
            arg = stringMethodArgumentHandler.securityChecks(arg, servletRequest, parameter);
        }
        super.handleResolvedValue(arg, name, parameter, mavContainer, request);
    }
}
