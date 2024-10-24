package org.suda.util;


import java.util.Objects;

/**
 * 封装工具类：
 * {@link org.apache.commons.lang3.StringUtils}
 * @author chengshaozhuang
 * @dateTime 2024-10-13 22:39
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
     * @date 2024-10-24 15:59
     * @param strings
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
}
