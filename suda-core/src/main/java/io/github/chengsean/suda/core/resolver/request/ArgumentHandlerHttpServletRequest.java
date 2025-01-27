package io.github.chengsean.suda.core.resolver.request;

import io.github.chengsean.suda.core.handler.MethodArgumentHandler;
import io.github.chengsean.suda.core.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;

/**
 *
 * @author chengshaozhuang
 * @dateTime 2025-01-18 03:24
 */
public class ArgumentHandlerHttpServletRequest extends HttpServletRequestWrapper {

    private final MethodArgumentHandler stringMethodArgumentHandler;
    private final MethodArgumentHandler fileMethodArgumentHandler;
    private final HttpServletRequest request;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request the {@link HttpServletRequest} to be wrapped.
     * @throws IllegalArgumentException if the request is null
     */
    public ArgumentHandlerHttpServletRequest(HttpServletRequest request, MethodArgumentHandler stringMethodArgumentHandler,
                                             MethodArgumentHandler fileMethodArgumentHandler) {
        super(request);
        this.stringMethodArgumentHandler = stringMethodArgumentHandler;
        this.fileMethodArgumentHandler = fileMethodArgumentHandler;
        this.request = request;
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        Collection<Part> parts = super.getParts();
        if (parts == null) {
            return null;
        }
        for (Part part : parts) {
            if (StringUtils.isNotBlank(part.getName())) {
                fileMethodArgumentHandler.securityChecks(part, request, null);
            }
        }
        return parts;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return super.getPart(name);
    }

    @Override
    public String getParameter(String name) {
        return super.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            values[i] = (String) stringMethodArgumentHandler.securityChecks(value, request, null);
        }
        return values;
    }
}
