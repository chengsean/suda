package org.suda.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.suda.config.SudaProperties;
import org.suda.exception.DangerousOperationsException;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.util.AntPathMatcher;
import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
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
    public Object securityChecks(Object arg, HttpServletRequest request, @Nullable MethodParameter parameter) {
        return securityChecks0(arg, Objects.requireNonNull(request).getServletPath(), parameter);
    }

    @SuppressWarnings("unchecked")
    private Object securityChecks0(Object arg, String servletPath, MethodParameter parameter) {
        if (arg instanceof String) {
            return handleInjection4Str(arg.toString(), servletPath);
        }
        // 检查是否为String数组
        boolean isStringArray = parameter != null ? String.class == parameter.getNestedParameterType().getComponentType()
                : arg instanceof String[];
        if (isStringArray) {
            handleInjection4StrArray((String[])arg, servletPath);
        }
        if (arg instanceof Map) {
            return handleInjection4Map((Map<Object, Object>) arg, servletPath);
        }
        if (arg instanceof Collection) {
            return handleInjection4Collection((Collection<Object>) arg, servletPath);
        }
        return handleSecurity4Object(arg, servletPath);
    }

    private void handleInjection4StrArray(String[] strings, String servletPath) {
        if (strings == null) {
            return;
        }
        for (int i = 0; i < strings.length; i++) {
            String str = strings[i];
            if (enabledSqlInjection(servletPath)) {
                strings[i] = handleSQLInjection(str);
            }
            if (enabledXxsInjection(servletPath)) {
                strings[i] = handleXSSInjection(str);
            }
            if (str != null && properties.getChars().isTrimEnabled()) {
                strings[i] = str.trim();
            }
        }
    }

    private Object handleSecurity4Object(Object arg, String servletPath) {
        if (arg == null) {
            return null;
        }
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(arg.getClass());
        for (PropertyDescriptor pd : propertyDescriptors) {
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();
            if (readMethod != null && writeMethod != null) {
                try {
                    Object value = readMethod.invoke(arg);
                    if (value instanceof String) {
                        writeMethod.invoke(arg, handleInjection4Str(value.toString(), servletPath));
                    }
                } catch (IllegalAccessException | InvocationTargetException ignore) {
                    // ignore 'getXXX' and 'setXXX' method Invocation exception
                }
            }
        }
        return arg;
    }

    private Collection<Object> handleInjection4Collection(Collection<Object> collection, String servletPath) {
        if (collection == null) {
            return null;
        }
        Object[] objects = collection.toArray(new Object[0]);
        collection.clear();
        for (Object object : objects) {
            if (object instanceof String) {
                collection.add(handleInjection4Str(object.toString(), servletPath));
            } else {
                collection.add(object);
            }
        }
        return collection;
    }

    private Map<Object, Object> handleInjection4Map(Map<Object, Object> map, String servletPath) {
        if (map == null) {
            return null;
        }
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                map.put(entry.getKey(), handleInjection4Str(value.toString(), servletPath));
            }
        }
        return map;
    }

    private Object handleInjection4Str(String str, String servletPath) {
        if (enabledSqlInjection(servletPath)) {
            str = handleSQLInjection(str);
        }
        if (enabledXxsInjection(servletPath)) {
            str = handleXSSInjection(str);
        }
        if (str != null) {
            return properties.getChars().isTrimEnabled() ? str.trim() : str;
        }
        return null;
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
                throw new DangerousOperationsException("检测到入参：'"+str+"'有安全风险问题！");
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
