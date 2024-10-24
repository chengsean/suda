package org.suda.resolver;

import org.springframework.web.bind.annotation.ResponseBody;
import org.suda.handler.MethodArgumentHandler;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 该对象作用于{@link RequestBody}和{@link ResponseBody}注解参数的安全检查
 * @author chengshaozhuang
 * @dateTime 2023-08-03 00:25
 */
public class SecurityRequestResponseBodyMethodProcessor extends RequestResponseBodyMethodProcessor {

    private final MethodArgumentHandler stringMethodArgumentHandler;

    public SecurityRequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters,
                                                      @Nullable List<Object> requestResponseBodyAdvice,
                                                      MethodArgumentHandler stringMethodArgumentHandler) {
        super(converters, requestResponseBodyAdvice);
        this.stringMethodArgumentHandler = stringMethodArgumentHandler;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object arg = super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        return stringMethodArgumentHandler.securityChecks(arg, servletRequest, parameter);
    }
}