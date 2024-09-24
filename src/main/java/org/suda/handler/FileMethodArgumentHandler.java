package org.suda.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.suda.config.SudaProperties;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import org.suda.util.TikaWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 文件类型参数安全检查
 * @author chengshaozhuang
 * @dateTime 2024-08-01 13:37
 */
public class FileMethodArgumentHandler implements MethodArgumentHandler {
    protected final Logger logger = LoggerFactory.getLogger(FileMethodArgumentHandler.class);
    private final SudaProperties properties;
    private final TikaWrapper tikaWrapper;

    public FileMethodArgumentHandler(SudaProperties properties, TikaWrapper tikaWrapper) {
        this.properties = properties;
        this.tikaWrapper = tikaWrapper;
    }


    @Override
    @Nullable
    public Object securityChecks(@Nullable Object arg, @Nullable HttpServletRequest request, MethodParameter parameter) {
        return securityChecks0(arg, request, parameter);
    }


    @SuppressWarnings({"unchecked","rawtypes"})
    private Object securityChecks0(Object arg, HttpServletRequest request, MethodParameter parameter) {
        if (arg == null || request == null) {
            return null;
        }
        if (!fileSecurityChecksEnabled(request.getServletPath())) {
            return arg;
        }
        if (MultipartFile.class == parameter.getNestedParameterType()) {
            checkFileType((MultipartFile) arg);
        }
        else if (isMultipartFileCollection(parameter)) {
            Collection<MultipartFile> files = (Collection<MultipartFile>) arg;
            checkFileType(files.toArray(new MultipartFile[0]));
        }
        else if (isMultipartFileArray(parameter)) {
            checkFileType((MultipartFile[]) arg);
        }
        else if (Part.class == parameter.getNestedParameterType()) {
            checkFileType((Part) arg);
        }
        else if (isPartCollection(parameter)) {
            Collection<Part> parts = (Collection<Part>) arg;
            checkFileType(parts.toArray(new Part[0]));
        }
        else if (isPartArray(parameter)) {
            checkFileType((Part[]) arg);
        }
        else if (MultiValueMap.class.isAssignableFrom(parameter.getParameterType())) {
            Class<?> valueType = getValueType(parameter, MultiValueMap.class);
            checkFileType(valueType, ((MultiValueMap)(arg)).values().toArray());
        }
        else if (Map.class.isAssignableFrom(parameter.getParameterType())) {
            Class<?> valueType = getValueType(parameter, Map.class);
            checkFileType(valueType, ((Map)(arg)).values().toArray());
        }
        return arg;
    }

    private void checkFileType(Class<?> valueType, Object... arg) {
        if (MultipartFile.class == valueType) {
            checkFileType((MultipartFile[]) arg);
        }
        if (valueType == Part.class) {
            checkFileType((Part[]) arg);
        }
    }

    private Class<?> getValueType(MethodParameter parameter, Class<?> wrapperClass) {
        ResolvableType resolvableType = ResolvableType.forMethodParameter(parameter);
        return resolvableType.as(wrapperClass).getGeneric(1).resolve();
    }


    private void checkFileType(Part... parts) {
        for (Part part : parts) {
            if (part == null) {
                continue;
            }
            String filename = part.getSubmittedFileName();
            try {
                // 检查文件的真实类型
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
        return fileCheckEnabled && !servletPathWhitelist.contains(servletPath);
    }

    private boolean isMultipartFileCollection(MethodParameter methodParam) {
        return (MultipartFile.class == getCollectionParameterType(methodParam));
    }

    private boolean isMultipartFileArray(MethodParameter methodParam) {
        return (MultipartFile.class == methodParam.getNestedParameterType().getComponentType());
    }

    private boolean isPartCollection(MethodParameter methodParam) {
        return (Part.class == getCollectionParameterType(methodParam));
    }

    private boolean isPartArray(MethodParameter methodParam) {
        return (Part.class == methodParam.getNestedParameterType().getComponentType());
    }

    @Nullable
    private Class<?> getCollectionParameterType(MethodParameter methodParam) {
        Class<?> paramType = methodParam.getNestedParameterType();
        if (Collection.class == paramType || List.class.isAssignableFrom(paramType)){
            return ResolvableType.forMethodParameter(methodParam).asCollection().resolveGeneric();
        }
        return null;
    }
}
