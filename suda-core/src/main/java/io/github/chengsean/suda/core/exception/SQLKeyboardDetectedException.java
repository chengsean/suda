package io.github.chengsean.suda.core.exception;

/**
 * SQL关键词检出异常，入参包含SQL关键词，可能存在SQL注入攻击行为
 * @author chengshaozhuang
 */
public class SQLKeyboardDetectedException extends RuntimeException {

    public SQLKeyboardDetectedException(String message) {
        super(message);
    }
}
