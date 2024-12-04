package io.github.chengsean.suda.core.exception;

/**
 * 有安全隐患的文件类型异常
 * @author chengshaozhuang
 */
public class DangerousFileTypeException extends RuntimeException {

    public DangerousFileTypeException(String message) {
        super(message);
    }
}
