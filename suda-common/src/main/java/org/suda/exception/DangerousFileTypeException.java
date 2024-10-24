package org.suda.exception;

/**
 * 有安全隐患的文件类型异常
 * @author chengshaozhuang
 * @dateTime 2024-08-05 16:45
 */
public class DangerousFileTypeException extends RuntimeException {

    public DangerousFileTypeException(String message) {
        super(message);
    }
}
