package org.suda.resolver;

import org.suda.handler.MethodArgumentHandler;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.NativeWebRequest;
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
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        Object arg = super.resolveName(name, parameter, request);
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        return stringMethodArgumentHandler.securityChecks(arg, servletRequest, parameter);
    }
}
