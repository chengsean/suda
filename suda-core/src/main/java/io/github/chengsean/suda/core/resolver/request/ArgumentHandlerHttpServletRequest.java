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
                securityCheckFile(part);
            }
        }
        return parts;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        Part part = super.getPart(name);
        securityCheckFile(part);
        return part;
    }


    private void securityCheckFile(Part part) {
        fileMethodArgumentHandler.securityChecks(part, request, null);
    }

    @Override
    public String getParameter(String name) {
        return securityCheckString(super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            values[i] = securityCheckString(value);
        }
        return values;
    }

    private String securityCheckString(String value) {
        return (String) stringMethodArgumentHandler.securityChecks(value, request, null);
    }
}
