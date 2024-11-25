package org.suda.core.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.suda.core.exception.SQLKeyboardDetectedException;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.suda.core.util.ServletRequestUtils;
import org.suda.core.util.StringEscapeUtils;

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
 */
public class StringMethodArgumentHandler implements MethodArgumentHandler {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final ArgumentHandlerProperties properties;

    public StringMethodArgumentHandler(ArgumentHandlerProperties properties) {
        this.properties = properties;
    }

    @Override
    @Nullable
    public Object securityChecks(@Nullable Object arg, HttpServletRequest request, @Nullable MethodParameter parameter) {
        String message = "Instance of ttpServletRequest can't be null";
        return securityChecks0(arg, Objects.requireNonNull(request, message), parameter);
    }

    @SuppressWarnings("unchecked")
    private Object securityChecks0(@Nullable Object arg, HttpServletRequest request, @Nullable MethodParameter parameter) {
        if (arg == null) {
            return null;
        }
        String servletPath = ServletRequestUtils.getServletPath(request);
        if (arg instanceof String) {
            return securityCheck4SimpleString(arg.toString(), servletPath);
        }
        // String数组
        boolean isStringArray = String.class == arg.getClass().getComponentType() ||
                parameter != null && String.class == parameter.getNestedParameterType().getComponentType();
        if (isStringArray) {
            securityCheck4StringArray((String[])arg, servletPath);
        }
        if (arg instanceof MultiValueMap) {
            return securityCheck4MultiValueMap((MultiValueMap<Object, Object>) arg, servletPath);
        }
        if (arg instanceof Map) {
            return securityCheck4Map((Map<Object, Object>) arg, servletPath);
        }
        return securityCheck4Object(arg, servletPath);
    }

    private Object securityCheck4MultiValueMap(MultiValueMap<Object, Object> map, String servletPath) {
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
                map.add(entry.getKey(), securityCheck4SimpleString(value.toString(), servletPath));
            }
        }
        return map;
    }

    private void securityCheck4StringArray(String[] strings, String servletPath) {
        for (int i = 0; i < strings.length; i++) {
            String str = strings[i];
            Object obj = securityCheck4SimpleString(str, servletPath);
            strings[i] = Objects.toString(obj, null);
        }
    }

    private Object securityCheck4Object(Object arg, String servletPath) {
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
                    writeMethod.invoke(arg, securityCheck4SimpleString(value, servletPath));
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.warn("Opos!! Calling the '{}' method encountered an unexpected exception.", writeMethod.getName(), e);
            }
        }
        return arg;
    }

    private Map<Object, Object> securityCheck4Map(Map<Object, Object> map, String servletPath) {
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                try {
                    map.put(entry.getKey(), securityCheck4SimpleString(value.toString(), servletPath));
                } catch (UnsupportedOperationException e) {
                    logger.warn("The map '{}' cannot be modified.", map.getClass().getName(), e);
                   // return value when can not edit this map
                    return map;
                }
            }
        }
        return map;
    }

    private Object securityCheck4SimpleString(String arg, String servletPath) {
        if (arg == null) {
            return null;
        }
        if (enabledSqlInjectionSecurityCheck(servletPath)) {
            arg = checkSQLInjection(arg);
        }
        if (enabledXxsInjectionSecurityCheck(servletPath)) {
            arg = checkXSSInjection(arg);
        }
        return properties.getChars().isTrimEnabled() ? arg.trim() : arg;
    }

    private boolean enabledXxsInjectionSecurityCheck(String servletPath) {
        return properties.getXssAttack().isCheckEnabled() &&
                ServletRequestUtils.isNotOnWhitelist(properties.getXssAttack().getServletPathWhitelist(), servletPath);
    }

    private boolean enabledSqlInjectionSecurityCheck(String servletPath) {
        return properties.getSqlInject().isCheckEnabled() &&
                ServletRequestUtils.isNotOnWhitelist(properties.getSqlInject().getServletPathWhitelist(), servletPath);
    }

    protected String checkSQLInjection(String arg) {
        String[] sqlKeywordList = properties.getSqlInject().getSqlKeywordList();
        if (arg == null || sqlKeywordList == null) {
            return null;
        }
        for (String keyword : sqlKeywordList) {
            if (arg.contains(keyword)) {
                logger.warn("Parameter: '{}' is detected to contain an SQL keyword！", arg);
                throw new SQLKeyboardDetectedException("Parameter: '"+arg+"' is detected to contain an SQL keyword！");
            }
        }
        return arg;
    }

    protected String checkXSSInjection(String arg) {
        return escapeHtml(arg);
    }

    private String escapeHtml(String arg) {
        String[] xssRegexList = properties.getXssAttack().getXssRegexList();
        if (arg == null || xssRegexList == null) {
            return null;
        }
        for (String regex : xssRegexList) {
            if (arg.matches(regex)) {
                return StringEscapeUtils.escapeHtml4(arg);
            }
        }
        return arg;
    }

    public ArgumentHandlerProperties getProperties() {
        return properties;
    }
}
