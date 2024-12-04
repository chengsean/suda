package io.github.chengsean.suda.core.exception;

/**
 * 非法的文件类型异常：文件扩展名被篡改
 * @author chengshaozhuang
 */
public class IllegalFileTypeException extends RuntimeException {

    public IllegalFileTypeException(String message) {
        super(message);
    }
}
