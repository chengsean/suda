package org.suda.util;


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
}
