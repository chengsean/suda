package org.suda;

import org.springframework.web.multipart.support.MultipartResolutionDelegate;
import org.suda.handler.MethodArgumentHandler;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

/**
 * {@link RequestParamMethodArgumentResolver}扩展类，检测和处理{@link RequestMapping}注解的方法中带有{@link RequestParam}注解的{@link String}对象、
 * 以及无任何注解的{@link String}、{@link MultipartFile}、{@link Part}对象，支持的String参数类型：String，包装类中的String、Map<Object, String>、List<String>等。
 * @author chengshaozhuang
 * @dateTime 2023-07-29 20:13
 */
public class SecurityRequestParamMethodArgumentResolver extends RequestParamMethodArgumentResolver {

    private final MethodArgumentHandler stringMethodArgumentHandler;
    private final MethodArgumentHandler fileMethodArgumentHandler;

    public SecurityRequestParamMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory, boolean useDefaultResolution,
                                                      MethodArgumentHandler stringMethodArgumentHandler, MethodArgumentHandler fileMethodArgumentHandler) {
        super(beanFactory, useDefaultResolution);
        this.stringMethodArgumentHandler = stringMethodArgumentHandler;
        this.fileMethodArgumentHandler = fileMethodArgumentHandler;
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest webRequest) throws Exception {
        Object arg = super.resolveName(name, parameter, webRequest);
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (servletRequest == null) {
            return arg;
        }
        if (MultipartResolutionDelegate.isMultipartArgument(parameter)) {
            fileMethodArgumentHandler.securityChecks(arg, servletRequest, parameter);
        }
        return stringMethodArgumentHandler.securityChecks(arg, servletRequest, parameter);
    }
}
