package io.github.chengsean.suda.core.util;

import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author chengshaozhuang
 */
public abstract class ServletRequestUtils {

    public static String getServletPath(@NonNull HttpServletRequest request) {
        String servletPath = Objects.requireNonNull(request).getServletPath();
        return StringUtils.isNotBlank(servletPath) ? servletPath : request.getRequestURI();
    }

    public static boolean isNotOnWhitelist(List<String> servletPathWhitelist, String servletPath) {
        if (servletPathWhitelist == null || servletPath == null) {
            return true;
        }
        AntPathMatcher matcher = new AntPathMatcher();
        for (String url : servletPathWhitelist) {
            boolean match = Objects.nonNull(url) && matcher.match(url, servletPath);
            if (match) {
                return false;
            }
        }
        return true;
    }
}
