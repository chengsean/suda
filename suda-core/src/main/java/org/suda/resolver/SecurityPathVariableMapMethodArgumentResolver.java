package org.suda.resolver;

import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
 * -------------------------------------------------------------
 * 经过多方考虑和斟酌，当前功能暂不支持，后续有需求再进行相应处理（2024-10-21）。 原因如下：
 * 由于入参{@link Map}对象会转换成字符串放在另一个{@link Map}（即包装/容器类嵌套结构数据）。由于此类参数处理起来会比较复杂，
 * 而且有更方便的替代处理方式如：{@link RequestParam}、{@link MatrixVariable}、{@link RequestBody}等。
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
