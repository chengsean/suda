package org.suda.common;

import java.io.Serializable;

/**
 * 返回值的包装类
 * @author chengshaozhuang
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    private boolean success = true;

    /**
     * 返回消息
     */
    private String message = "";

    /**
     * 返回状态码
     */
    private Integer code = 0;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 时间戳
     */
    private final long timestamp = System.currentTimeMillis();

    public Result() {
    }

    public static<T> Result<T> OK() {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(Constant.SC_OK);
        return result;
    }

    public static<T> Result<T> OK(T data) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setSuccess(true);
        result.setCode(Constant.SC_OK);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", code=" + code +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}
