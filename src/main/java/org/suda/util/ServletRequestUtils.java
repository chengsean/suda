package org.suda.util;

import org.springframework.lang.NonNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 *
 * @author chengshaozhuang
 * @dateTime 2024-10-14 15:37
 */
public abstract class ServletRequestUtils {

    public static String getServletPath(@NonNull HttpServletRequest request) {
        Objects.requireNonNull(request);
        String servletPath = request.getServletPath();
        return StringUtils.isNotBlank(servletPath) ? servletPath : request.getRequestURI();
    }
}
