package org.suda.common.exception;

/**
 * SQL关键词检出异常，入参包含SQL关键词，可能存在SQL注入攻击行为
 * @author chengshaozhuang
 * @dateTime 2024-07-16 20:55
 */
public class SQLKeyboardDetectedException extends RuntimeException {

    public SQLKeyboardDetectedException(String message) {
        super(message);
    }
}
