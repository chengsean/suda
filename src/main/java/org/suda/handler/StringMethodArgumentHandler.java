package org.suda.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.suda.config.SudaProperties;
import org.suda.exception.SQLKeyboardDetectedException;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.util.AntPathMatcher;
import org.suda.util.ServletRequestUtils;
import org.suda.util.StringEscapeUtils;
import org.suda.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 方法字符串参数安全检查，通过重写这些方法来实现：{@link HandlerMethodArgumentResolver#resolveArgument(MethodParameter, ModelAndViewContainer, NativeWebRequest, WebDataBinderFactory)}
 * 或者 {@link org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver#resolveName(String, MethodParameter, NativeWebRequest)}
 * 又或者 {@link org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver#handleResolvedValue(Object, String, MethodParameter, ModelAndViewContainer, NativeWebRequest)}
 * @author chengshaozhuang
 * @dateTime 2024-07-12 16:08
 */
public class StringMethodArgumentHandler implements MethodArgumentHandler {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final SudaProperties properties;

    public StringMethodArgumentHandler(SudaProperties properties) {
        this.properties = properties;
    }

    @Override
    @Nullable
    public Object securityChecks(@Nullable Object arg, @Nullable HttpServletRequest request, @Nullable MethodParameter parameter) {
        return securityChecks0(arg, request, parameter);
    }

    @SuppressWarnings("unchecked")
    private Object securityChecks0(Object arg, HttpServletRequest request, @Nullable MethodParameter parameter) {
        if (arg == null || request == null) {
            return null;
        }
        String servletPath = ServletRequestUtils.getServletPath(request);
        if (arg instanceof String) {
            return handleInjection4Str(arg.toString(), servletPath);
        }
        // String数组
        boolean isStringArray = String.class == arg.getClass().getComponentType() ||
                parameter != null && String.class == parameter.getNestedParameterType().getComponentType();
        if (isStringArray) {
            handleInjection4StrArray((String[])arg, servletPath);
        }
        if (arg instanceof MultiValueMap) {
            return handleInjection4MultiValueMap((MultiValueMap<Object, Object>) arg, servletPath);
        }
        if (arg instanceof Map) {
            return handleInjection4Map((Map<Object, Object>) arg, servletPath);
        }
        return handleSecurity4Object(arg, servletPath);
    }

    private Object handleInjection4MultiValueMap(MultiValueMap<Object, Object> map, String servletPath) {
        for (Map.Entry<Object, List<Object>> entry : map.entrySet()) {
            List<Object> values = entry.getValue();
            if (values.isEmpty() || !(values.get(0) instanceof String)) {
                return map;
            }
            Object[] objects = entry.getValue().toArray();
            try {
                values.clear();
            } catch (UnsupportedOperationException e) {
                logger.warn("The map '{}' cannot be modified.", map.getClass().getName(), e);
                return map;
            }
            for (Object value : objects) {
                map.add(entry.getKey(), handleInjection4Str(value.toString(), servletPath));
            }
        }
        return map;
    }

    private void handleInjection4StrArray(String[] strings, String servletPath) {
        for (int i = 0; i < strings.length; i++) {
            String str = strings[i];
            Object obj = handleInjection4Str(str, servletPath);
            strings[i] = Objects.toString(obj, null);
        }
    }

    private Object handleSecurity4Object(Object arg, String servletPath) {
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(arg.getClass());
        for (PropertyDescriptor pd : propertyDescriptors) {
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();
            if (readMethod == null || writeMethod == null) {
                continue;
            }
            try {
                Class<?> returnType = readMethod.getReturnType();
                boolean isStringType = returnType.isAssignableFrom(String.class);
                if (isStringType) {
                    String value = Objects.toString(readMethod.invoke(arg), null);
                    writeMethod.invoke(arg, handleInjection4Str(value, servletPath));
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.warn("Opos!! Calling the '{}' method encountered an unexpected exception.", writeMethod.getName(), e);
            }
        }
        return arg;
    }

    private Map<Object, Object> handleInjection4Map(Map<Object, Object> map, String servletPath) {
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                try {
                    map.put(entry.getKey(), handleInjection4Str(value.toString(), servletPath));
                } catch (UnsupportedOperationException e) {
                    logger.warn("The map '{}' cannot be modified.", map.getClass().getName(), e);
                   // return value when can not edit this map
                    return map;
                }
            }
        }
        return map;
    }

    private Object handleInjection4Str(String str, String servletPath) {
        if (str == null) {
            return null;
        }
        if (enabledSqlInjection(servletPath)) {
            str = handleSQLInjection(str);
        }
        if (enabledXxsInjection(servletPath)) {
            str = handleXSSInjection(str);
        }
        return properties.getChars().isTrimEnabled() ? str.trim() : str;
    }

    private boolean enabledXxsInjection(String servletPath) {
        return properties.getXssAttack().isCheckEnabled() && !inXssWhitelist(servletPath);
    }

    private boolean enabledSqlInjection(String servletPath) {
        return properties.getSqlInject().isCheckEnabled() && !inSqlInjectWhitelist(servletPath);
    }

    private boolean inSqlInjectWhitelist(String servletPath) {
        return properties.getSqlInject().getServletPathWhitelist().contains(servletPath);
    }

    protected String handleSQLInjection(String str) {
        if (str == null) {
            return null;
        }
        String[] sqlKeywordList = properties.getSqlInject().getSqlKeywordList();
        for (String keyword : sqlKeywordList) {
            if (str.contains(keyword)) {
                logger.warn("检测到入参：'{}'有安全风险问题！", str);
                throw new SQLKeyboardDetectedException("检测到入参：'"+str+"'有安全风险问题！");
            }
        }
        return str;
    }

    protected String handleXSSInjection(String str) {
        return escapeHtml(str);
    }

    private String escapeHtml(String str) {
        if (str == null) {
            return null;
        }
        String[] xssRegexList = properties.getXssAttack().getXssRegexList();
        for (String regex : xssRegexList) {
            if (str.matches(regex)) {
                return StringEscapeUtils.escapeHtml4(str);
            }
        }
        return str;
    }

    private boolean inXssWhitelist(String servletPath) {
        if (StringUtils.isBlank(servletPath)) {
            throw new IllegalArgumentException("请求路径不能为空");
        }
        AntPathMatcher matcher = new AntPathMatcher();
        String[] xssRegexList = properties.getXssAttack().getXssRegexList();
        for (String url : xssRegexList) {
            boolean match = Objects.nonNull(url) && matcher.match(url, servletPath);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
