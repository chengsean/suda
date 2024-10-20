package org.suda.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.suda.config.ArgumentResolverConfiguration;
import org.suda.exception.SQLKeyboardDetectedException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.*;

/**
 * 字符串安全检查单元测试
 * @author chengshaozhuang
*/
@SpringBootTest(classes = {ArgumentResolverConfiguration.class})
class StringMethodArgumentHandlerTests {

    final String comment = "<script>alert(\"chengshaozhuang\")</script>";
    final String nickname = "Gengshao select * from mysql.user;";

    @Resource
    private MethodArgumentHandler stringMethodArgumentHandler;

    private HttpServletRequest request4UriWhitelist;
    private HttpServletRequest request4NotUriWhitelist;

    @BeforeEach
    void contextLoads() {
        request4UriWhitelist = new MockHttpServletRequest(null,"/example");
        request4NotUriWhitelist = new MockHttpServletRequest(null,"/index");
    }

    @Test
    void testXSSAttacksCheck4UriOnWhitelist() {
        // 测试白名单接口的XSS攻击的安全检查
        Object value = stringMethodArgumentHandler.securityChecks(comment, request4UriWhitelist, null);
        assertThat(value).isEqualTo(comment);
    }

    @Test
    void testXSSAttacksCheck4UriNotOnWhitelist() {
        // 测试非白名单接口的XSS攻击的安全检查（转换实体字符）
        Object value = stringMethodArgumentHandler.securityChecks(comment, request4NotUriWhitelist, null);
        assertThat(value).isNotEqualTo(comment);
    }

    @Test
    void testSQLInjectionAttacksCheck4UriOnWhitelist() {
        // 测试白名单接口的SQL注入攻击的安全检查
        Object value = stringMethodArgumentHandler.securityChecks(nickname, request4UriWhitelist, null);
        assertThat(value).isEqualTo(nickname);
    }

    @Test
    void testSQLInjectionAttacksCheck4UriNotOnWhitelist() {
        // 测试非白名单接口的SQL注入攻击的安全检查
        assertThatThrownBy(()-> stringMethodArgumentHandler.securityChecks(
                nickname, request4NotUriWhitelist, null)).isInstanceOf(SQLKeyboardDetectedException.class);
    }
}
