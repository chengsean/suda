package org.suda.core.handler;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import javax.servlet.http.HttpServletRequest;

/**
 * 方法参数检查处理
 * @author chengshaozhuang
 * @dateTime 2024-08-02 15:49
 */
public interface MethodArgumentHandler {


    @Nullable
    Object securityChecks(@Nullable Object arg, HttpServletRequest request, @Nullable MethodParameter parameter);
}
