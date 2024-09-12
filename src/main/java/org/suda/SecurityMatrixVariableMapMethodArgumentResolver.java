package org.suda;

import org.suda.handler.MethodArgumentHandler;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMapMethodArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 该对象作用于{@link MatrixVariable} 注解{@link Map}参数的安全检查
 * @author chengshaozhuang
 * @dateTime 2024-07-29 10:46
 */
public class SecurityMatrixVariableMapMethodArgumentResolver extends MatrixVariableMapMethodArgumentResolver {

    private final MethodArgumentHandler stringMethodArgumentHandler;

    public SecurityMatrixVariableMapMethodArgumentResolver(MethodArgumentHandler stringMethodArgumentHandler) {
        this.stringMethodArgumentHandler = stringMethodArgumentHandler;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {
        Object arg = super.resolveArgument(parameter, mavContainer, request, binderFactory);
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        if (servletRequest != null) {
            return stringMethodArgumentHandler.securityChecks(arg, servletRequest, parameter);
        }
        return arg;
    }
}
