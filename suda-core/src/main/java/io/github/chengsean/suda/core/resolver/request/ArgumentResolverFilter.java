package io.github.chengsean.suda.core.resolver.request;

import io.github.chengsean.suda.core.handler.MethodArgumentHandler;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * description
 *
 * @author chengshaozhuang
 * @dateTime 2025-01-21 01:36
 */
public class ArgumentResolverFilter implements Filter {

    private final MethodArgumentHandler stringMethodArgumentHandler;
    private final MethodArgumentHandler fileMethodArgumentHandler;

    public ArgumentResolverFilter(MethodArgumentHandler stringMethodArgumentHandler,
                                  MethodArgumentHandler fileMethodArgumentHandler) {
        this.stringMethodArgumentHandler = stringMethodArgumentHandler;
        this.fileMethodArgumentHandler = fileMethodArgumentHandler;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        ArgumentHandlerHttpServletRequest httpServletRequest = new ArgumentHandlerHttpServletRequest(servletRequest,
                stringMethodArgumentHandler, fileMethodArgumentHandler);
        chain.doFilter(httpServletRequest, response);
    }

//    private boolean containsUriWhitelist(String requestURI) {
//        properties.getFiles().
//        return false;
//    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
