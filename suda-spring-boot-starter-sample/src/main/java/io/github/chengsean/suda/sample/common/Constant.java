package io.github.chengsean.suda.sample.common;

/**
 * 常量类
 * @author chengshaozhuang
 */
public interface Constant {
    /** {@code 200 OK} (HTTP/1.0 - RFC 1945) */
    int SC_OK = 200;

    String HOST = "http://localhost";

    int PORT = 8080;

    /** 测试接口前缀 */
    String PREFIX_SERVLET_PATH = "/sample";

    String STRING_PREFIX_SERVLET_PATH = PREFIX_SERVLET_PATH + "/string";

    String FILE_PREFIX_SERVLET_PATH = PREFIX_SERVLET_PATH + "/file";

    String NAME_KEY = "name";

    String NAME_VALUE = " chengshaozhuang   ";

    String EMAIL_KEY = "email";

    String EMAIL_VALUE = "520032191110242048@gmail.com";
}
