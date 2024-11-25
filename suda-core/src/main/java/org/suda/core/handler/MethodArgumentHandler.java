package org.suda.core.handler;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import javax.servlet.http.HttpServletRequest;

/**
 * 方法参数检查处理
 * @author chengshaozhuang
 */
public interface MethodArgumentHandler {


    @Nullable
    Object securityChecks(@Nullable Object arg, HttpServletRequest request, @Nullable MethodParameter parameter);
}
