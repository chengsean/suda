package org.suda.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.suda.config.SudaProperties;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import org.suda.util.TikaWrapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 文件类型方法参数安全检查
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
    public Object securityChecks(Object arg, HttpServletRequest request, @Nullable MethodParameter parameter) {
        return fileSecurityChecks(arg, Objects.requireNonNull(request).getServletPath(), parameter);
    }


    @SuppressWarnings("unchecked")
    private Object fileSecurityChecks(Object arg, String servletPath, MethodParameter parameter) {
        if (arg == null || !fileSecurityChecksEnabled(servletPath)) {
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
        return arg;
    }

    private void checkFileType(MultipartFile... files) {
        if (files == null) {
           return;
        }
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

    @Nullable
    private Class<?> getCollectionParameterType(MethodParameter methodParam) {
        Class<?> paramType = methodParam.getNestedParameterType();
        if (Collection.class == paramType || List.class.isAssignableFrom(paramType)){
            return ResolvableType.forMethodParameter(methodParam).asCollection().resolveGeneric();
        }
        return null;
    }
}
