package io.github.chengsean.suda.core.util;


import java.util.Objects;

/**
 * 封装工具类：
 * {@link org.apache.commons.lang3.StringUtils}
 * @author chengshaozhuang
 */
public abstract class StringUtils {

    public static boolean isBlank(final CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isBlank(cs);
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * 字符串前增加'.'字符
     * @author chengshaozhuang
     * @param strings strings
     * @return java.lang.String[]
     */
    public static String[] appendDotIfNecessary(String[] strings) {
        Objects.requireNonNull(strings, "strings can not be null");
        String[] normalized = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            String extension = strings[i];
            normalized[i] = extension.startsWith(".") ? extension : "." + extension;
        }
        return normalized;
    }

    public static String trim(String value) {
        return org.apache.commons.lang3.StringUtils.trim(value);
    }
}
