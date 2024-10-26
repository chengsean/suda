package org.suda.common.exception;

/**
 * 非法的文件类型异常：文件扩展名被篡改
 * @author chengshaozhuang
 * @dateTime 2024-09-16 16:06
 */
public class IllegalFileTypeException extends RuntimeException {

    public IllegalFileTypeException(String message) {
        super(message);
    }
}
