package org.suda.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import org.suda.handler.util.ServletRequestUtils;
import org.suda.tika.TikaWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 文件类型参数安全检查
 * @author chengshaozhuang
 * @dateTime 2024-08-01 13:37
 */
public class FileMethodArgumentHandler implements MethodArgumentHandler {
    protected final Logger logger = LoggerFactory.getLogger(FileMethodArgumentHandler.class);
    private final ArgumentHandlerProperties properties;
    private final TikaWrapper tikaWrapper;

    public FileMethodArgumentHandler(ArgumentHandlerProperties properties, TikaWrapper tikaWrapper) {
        this.properties = properties;
        this.tikaWrapper = tikaWrapper;
    }

    @Override
    @Nullable
    public Object securityChecks(@Nullable Object arg, HttpServletRequest request, @Nullable MethodParameter parameter) {
        String message = "Instance of ttpServletRequest can't be null";
        return securityChecks0(arg, Objects.requireNonNull(request, message), parameter);
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private Object securityChecks0(@Nullable Object arg, HttpServletRequest request, @Nullable MethodParameter parameter) {
        if (arg == null) {
            return null;
        }
        String servletPath = ServletRequestUtils.getServletPath(request);
        if (!fileSecurityChecksEnabled(servletPath)) {
            return arg;
        }
        if (arg instanceof MultipartFile) {
            checkFileType((MultipartFile) arg);
        }
        else if (isMultipartFileCollection(parameter, arg)) {
            Collection<MultipartFile> files = (Collection<MultipartFile>) arg;
            checkFileType(files.toArray(new MultipartFile[0]));
        }
        else if (isMultipartFileArray(parameter, arg)) {
            checkFileType((MultipartFile[]) arg);
        }
        else if (arg instanceof Part) {
            checkFileType((Part) arg);
        }
        else if (isPartCollection(parameter, arg)) {
            Collection<Part> parts = (Collection<Part>) arg;
            checkFileType(parts.toArray(new Part[0]));
        }
        else if (isPartArray(parameter, arg)) {
            checkFileType((Part[]) arg);
        }
        else if (isMultiValueMap(parameter, arg)) {
            ((MultiValueMap)arg).values().forEach(object -> checkFileType(((List<Object>) object).toArray()));
        }
        else if (isMap(parameter, arg)) {
            checkFileType(((Map)arg).values().toArray());
        }
        return arg;
    }

    private boolean isMap(MethodParameter parameter, Object arg) {
        if (parameter != null) {
            return Map.class.isAssignableFrom(parameter.getParameterType());
        }
        return Map.class.isAssignableFrom(arg.getClass());
    }

    private boolean isMultiValueMap(MethodParameter parameter, Object arg) {
        if (parameter != null) {
            return MultiValueMap.class.isAssignableFrom(parameter.getParameterType());
        }
        return MultiValueMap.class.isAssignableFrom(arg.getClass());
    }

    private void checkFileType(Object... objects) {
        for (Object object : objects) {
            if (object instanceof MultipartFile) {
                checkFileType((MultipartFile) object);
            }
            if (object instanceof Part) {
                checkFileType((Part) object);
            }
        }
    }

    private void checkFileType(Part... parts) {
        for (Part part : parts) {
            if (part == null) {
                continue;
            }
            String filename = part.getSubmittedFileName();
            try {
                // 文件安全检查
                tikaWrapper.checkFileType(filename, part.getInputStream());
            } catch (IOException e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Can not Read file '{}' error: {}", filename, e.getMessage());
                }
            }
        }
    }

    private void checkFileType(MultipartFile... files) {
        for (MultipartFile file : files) {
            if (file == null) {
                continue;
            }
            try {
                tikaWrapper.checkFileType(file.getOriginalFilename(), file.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                if (logger.isWarnEnabled()) {
                    logger.warn("Read file '{}' error: {}",file.getOriginalFilename(), e.getMessage());
                }
            }
        }
    }

    private boolean fileSecurityChecksEnabled(String servletPath) {
        List<String> servletPathWhitelist = properties.getFiles().getServletPathWhitelist();
        boolean fileCheckEnabled = properties.getFiles().isCheckEnabled();
        return fileCheckEnabled && ServletRequestUtils.isNotOnWhitelist(servletPathWhitelist, servletPath);
    }

    private boolean isMultipartFileCollection(MethodParameter methodParam, Object arg) {
        if (methodParam != null) {
            return MultipartFile.class == getCollectionParameterType(methodParam);
        }
        return MultipartFile.class.isAssignableFrom(arg.getClass());
    }

    private boolean isMultipartFileArray(MethodParameter methodParam, Object arg) {
        if (methodParam != null) {
            return (MultipartFile.class == methodParam.getNestedParameterType().getComponentType());
        }
        return MultipartFile.class.isAssignableFrom(arg.getClass());
    }

    private boolean isPartCollection(MethodParameter methodParam, Object arg) {
        if (methodParam != null) {
            return (Part.class == getCollectionParameterType(methodParam));
        }
        return Part.class.isAssignableFrom(arg.getClass());
    }

    private boolean isPartArray(MethodParameter methodParam, Object arg) {
        if (methodParam != null) {
            return (Part.class == methodParam.getNestedParameterType().getComponentType());
        }
        return Part.class.isAssignableFrom(arg.getClass());
    }

    @Nullable
    private Class<?> getCollectionParameterType(MethodParameter methodParam) {
        Class<?> paramType = methodParam.getNestedParameterType();
        if (Collection.class == paramType || List.class.isAssignableFrom(paramType)){
            return ResolvableType.forMethodParameter(methodParam).asCollection().resolveGeneric();
        }
        return null;
    }

    public ArgumentHandlerProperties getProperties() {
        return properties;
    }
}
