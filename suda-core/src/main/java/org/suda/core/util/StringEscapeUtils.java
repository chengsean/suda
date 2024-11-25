package org.suda.core.util;


/**
 * 封装工具类：
 * {@link org.apache.commons.text.StringEscapeUtils}
 * @author chengshaozhuang
 */
public abstract class StringEscapeUtils {

    public static String escapeHtml4(final String str) {
        return org.apache.commons.text.StringEscapeUtils.escapeHtml4(str);
    }
}
