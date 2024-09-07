package org.suda.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.suda.exception.DangerousOperationsException;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.util.AntPathMatcher;
import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 方法字符串参数安全检查，针对{@link HandlerMethodArgumentResolver#resolveArgument(MethodParameter, ModelAndViewContainer, NativeWebRequest, WebDataBinderFactory)}
 * 或者 {@link org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver#resolveName(String, MethodParameter, NativeWebRequest)}
 * 又或者 {@link org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver#handleResolvedValue(Object, String, MethodParameter, ModelAndViewContainer, NativeWebRequest)}等重写上述方法的子类对象
 * @author chengshaozhuang
 * @dateTime 2024-07-12 16:08
 */
public class StringMethodArgumentHandler implements MethodArgumentHandler {

    //    public static final String SQL_INJECT_LIST = "and |exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |;|or |+|user()";
    public static final String[] SQL_KEYWORD_LIST = new String[] {"and ","exec ","insert ","select ","delete ","update ","drop ","count ","chr ","mid ","master ","truncate ","char ","declare ",";|or ","+|user()"};
    public static final String[] XSS_REGEX_LIST = new String[] {"[\\S\\s\\t\\r\\n]*<[\\S\\s\\t\\r\\n]+(/)?>[\\S\\s\\t\\r\\n]*",
            "[\\S\\s\\t\\r\\n]*<[\\S\\s\\t\\r\\n]+>[\\S\\s\\t\\r\\n]+</[\\S\\s\\t\\r\\n]+>[\\S\\s\\t\\r\\n]*"};
    private final List<String> xssWhiteUrlList;

    public StringMethodArgumentHandler(String[] xssInjectionWhiteUrlList) {
        this.xssWhiteUrlList = Arrays.stream(Optional.ofNullable(
                xssInjectionWhiteUrlList).orElse(new String[0])).collect(Collectors.toList());
    }


    @Override
    public Object securityChecks(Object arg, HttpServletRequest request, MethodParameter parameter) {
        return securityChecks0(arg, Objects.requireNonNull(request).getServletPath(), parameter);
    }


    @SuppressWarnings("unchecked")
    public Object securityChecks0(Object arg, String servletPath, MethodParameter parameter) {
        if (arg instanceof String) {
            return handleInjection4Str(arg.toString(), servletPath);
        }
        // String[]
        if ((String.class == parameter.getNestedParameterType().getComponentType())) {
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
            if (str == null) {
                continue;
            }
            if (enabledSqlInjection(servletPath)) {
                strings[i] = handleSQLInjection(str);
            }
            if (enabledXxsInjection(servletPath)) {
                strings[i] = handleXSSInjection(str);
            }
            strings[i] = str.trim();
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
        if (str == null) {
            return null;
        }
        if (enabledSqlInjection(servletPath)) {
            str = handleSQLInjection(str);
        }
        if (enabledXxsInjection(servletPath)) {
            str = handleXSSInjection(str);
        }
        return str.trim();
    }

    private boolean enabledXxsInjection(String servletPath) {
        return !inXssWhiteList(servletPath);
    }

    private boolean enabledSqlInjection(String servletPath) {
        return !inSqlWhiteList(servletPath);
    }

    private boolean inSqlWhiteList(String servletPath) {
        return false;
    }

    protected String handleSQLInjection(String str) {
        if (str == null) {
            return null;
        }
        for (String keyword : SQL_KEYWORD_LIST) {
            if (keyword.contains(str)) {
                throw new DangerousOperationsException("检测到入参有安全风险的字符串！");
            }
        }
        return str;
    }

    protected String handleXSSInjection(String str) {
        if (str == null) {
            return null;
        }
        return escapeHtml(str);
    }

    private String escapeHtml(String str) {
        if (str == null) {
            return null;
        }
        for (String regex : XSS_REGEX_LIST) {
            if (str.matches(regex)) {
                return StringEscapeUtils.escapeHtml4(str);
            }
        }
        return str;
    }

    private boolean inXssWhiteList(String servletPath) {
        if (StringUtils.isBlank(servletPath)) {
            throw new IllegalArgumentException("请求路径不能为空");
        }
        AntPathMatcher matcher = new AntPathMatcher();
        for (String url : xssWhiteUrlList) {
            boolean match = Objects.nonNull(url) && matcher.match(url, servletPath);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
