package org.suda.resolver;

import org.suda.handler.MethodArgumentHandler;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.servlet.http.HttpServletRequest;

/**
 * 该对象作用于{@link ModelAttribute} 注解的参数的安全检查
 * @author chengshaozhuang
 * @dateTime 2023-08-06 12:03
 */
public class SecurityServletModelAttributeMethodProcessor extends ServletModelAttributeMethodProcessor {

    private final MethodArgumentHandler stringMethodArgumentHandler;

    /**
     * Class constructor.
     * @param annotationNotRequired if "true", non-simple method arguments and
     *                              return values are considered model attributes with or without a
     *                              {@code @ModelAttribute} annotation
     * @param stringMethodArgumentHandler  Argument Handler
     */
    public SecurityServletModelAttributeMethodProcessor(boolean annotationNotRequired, MethodArgumentHandler stringMethodArgumentHandler) {
        super(annotationNotRequired);
        this.stringMethodArgumentHandler = stringMethodArgumentHandler;
    }

    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest webRequest) {
        super.bindRequestParameters(binder, webRequest);
        Object attribute = binder.getTarget();
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        stringMethodArgumentHandler.securityChecks(attribute, servletRequest, null);
    }
}
