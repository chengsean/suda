package io.github.chengsean.suda.core.resolver;

import org.springframework.core.ResolvableType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import io.github.chengsean.suda.core.handler.MethodArgumentHandler;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.Map;

/**
 * 该对象作用于{@link RequestParam} 注解{@link Map}参数的安全检查
 * 支持:
 * <pre>{@code MultiValueMap<String,MultipartFile>、MultiValueMap<String,Part>、MultiValueMap<String,String>、
 * Map<String,MultipartFile>、Map<String,Part>、Map<String,String>}
 * </pre>
 * @author chengshaozhuang
 */
public class SecurityRequestParamMapMethodArgumentResolver extends RequestParamMapMethodArgumentResolver {


    private final MethodArgumentHandler stringMethodArgumentHandler;
    private final MethodArgumentHandler fileMethodArgumentHandler;

    public SecurityRequestParamMapMethodArgumentResolver(MethodArgumentHandler stringMethodArgumentHandler,
                                                         MethodArgumentHandler fileMethodArgumentHandler) {
        this.stringMethodArgumentHandler = stringMethodArgumentHandler;
        this.fileMethodArgumentHandler = fileMethodArgumentHandler;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        Object arg = super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (isMultipartArgument(parameter)) {
            return fileMethodArgumentHandler.securityChecks(arg, servletRequest, parameter);
        }
        return stringMethodArgumentHandler.securityChecks(arg, servletRequest, parameter);
    }

    private boolean isMultipartArgument(MethodParameter parameter) {
        ResolvableType resolvableType = ResolvableType.forMethodParameter(parameter);
        Class<?> valueType;
        if (MultiValueMap.class.isAssignableFrom(parameter.getParameterType())) {
            // MultiValueMap
            valueType = resolvableType.as(MultiValueMap.class).getGeneric(1).resolve();
        } else {
            // Regular Map
            valueType = resolvableType.asMap().getGeneric(1).resolve();
        }
        // File type or String[]
        return MultipartFile.class == valueType || Part.class == valueType;
    }
}