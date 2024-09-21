package org.suda;

import org.suda.handler.MethodArgumentHandler;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMethodArgumentResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * 该对象作用于{@link MatrixVariable} 注解参数的安全检查
 * @author chengshaozhuang
 * @dateTime 2024-07-29 11:21
 */
public class SecurityMatrixVariableMethodArgumentResolver extends MatrixVariableMethodArgumentResolver {

    private final MethodArgumentHandler stringMethodArgumentHandler;

    public SecurityMatrixVariableMethodArgumentResolver(MethodArgumentHandler stringMethodArgumentHandler) {
        this.stringMethodArgumentHandler = stringMethodArgumentHandler;
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        Object arg = super.resolveName(name, parameter, request);
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        return stringMethodArgumentHandler.securityChecks(arg, servletRequest, parameter);
    }
}
