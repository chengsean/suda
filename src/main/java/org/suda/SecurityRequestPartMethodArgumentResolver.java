package org.suda;

import org.suda.handler.MethodArgumentHandler;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.RequestPartMethodArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.List;

/**
 * 该对象作用于{@link RequestPart}注解的、或无注解的{@link MultipartFile}、{@link Part}(文件类型)参数的安全检查
 * @author chengshaozhuang
 * @dateTime 2024-08-02 10:11
 */
public class SecurityRequestPartMethodArgumentResolver extends RequestPartMethodArgumentResolver {

    private final MethodArgumentHandler fileMethodArgumentHandler;

    public SecurityRequestPartMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters,
                                                     List<Object> requestResponseBodyAdvice,
                                                     MethodArgumentHandler fileMethodArgumentHandler) {
        super(messageConverters, requestResponseBodyAdvice);
        this.fileMethodArgumentHandler = fileMethodArgumentHandler;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {
        Object arg = super.resolveArgument(parameter, mavContainer, request, binderFactory);
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        return fileMethodArgumentHandler.securityChecks(arg, servletRequest, parameter);
    }
}
