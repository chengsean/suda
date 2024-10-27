package org.suda.core.handler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.suda.autoconfigure.ArgumentHandlerConfiguration;
import org.suda.common.exception.SQLKeyboardDetectedException;
import org.suda.core.handler.MethodArgumentHandler;
import org.suda.core.handler.StringMethodArgumentHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

/**
 * 字符串安全检查单元测试{@link StringMethodArgumentHandler}
 * @author chengshaozhuang
*/
@SpringBootTest(classes = {ArgumentHandlerConfiguration.class})
class StringMethodArgumentHandlerTests {

    private final String comment = "<script>alert(\"chengshaozhuang\")</script>";
    private final String nickname = "Gengshao select * from mysql.user;";
    private final String uri = "/index";
    private final String uriWhitelist = "/example";

    @Resource
    private MethodArgumentHandler stringMethodArgumentHandler;

    private HttpServletRequest request;

    @Test
    void testXSSAttacksCheckEnabled() {
        // 测试XSS攻击的安全检查的启用状态
        toStringMethodArgumentHandler().getProperties().getXssAttack().setCheckEnabled(true);
        request = new MockHttpServletRequest(null, uri);
        Object value = stringMethodArgumentHandler.securityChecks(comment, request, null);
        assertThat(value).isNotEqualTo(comment);
    }

    private StringMethodArgumentHandler toStringMethodArgumentHandler() {
        return ((StringMethodArgumentHandler) stringMethodArgumentHandler);
    }

    @Test
    void testXSSAttacksCheckDisabled1() {
        // 测试XSS攻击的安全检查的禁用状态 1
        toStringMethodArgumentHandler().getProperties().getXssAttack().setCheckEnabled(false);
        toStringMethodArgumentHandler().getProperties().getXssAttack().setServletPathWhitelist(
                new ArrayList<>(Collections.singleton(uriWhitelist)));
        request = new MockHttpServletRequest(null, uriWhitelist);
        Object value = stringMethodArgumentHandler.securityChecks(comment, request, null);
        assertThat(value).isEqualTo(comment);
    }

    @Test
    void testXSSAttacksCheckDisabled2() {
        // 测试XSS攻击的安全检查的禁用状态 2
        toStringMethodArgumentHandler().getProperties().getXssAttack().setCheckEnabled(false);
        request = new MockHttpServletRequest(null, uri);
        Object value = stringMethodArgumentHandler.securityChecks(comment, request, null);
        assertThat(value).isEqualTo(comment);
    }

    @Test
    void testSQLInjectionAttacksCheckDisabled1() {
        // 测试SQL注入攻击的安全检查的启用状态
        toStringMethodArgumentHandler().getProperties().getSqlInject().setCheckEnabled(false);
        request = new MockHttpServletRequest(null, uri);
        Object value = stringMethodArgumentHandler.securityChecks(nickname, request, null);
        assertThat(value).isEqualTo(nickname);
    }

    @Test
    void testSQLInjectionAttacksCheckDisabled2() {
        // 测试SQL注入攻击的安全检查的启用状态
        toStringMethodArgumentHandler().getProperties().getSqlInject().setCheckEnabled(false);
        request = new MockHttpServletRequest(null, uriWhitelist);
        toStringMethodArgumentHandler().getProperties().getSqlInject().setServletPathWhitelist(
                new ArrayList<>(Collections.singleton(uriWhitelist)));
        Object value = stringMethodArgumentHandler.securityChecks(nickname, request, null);
        assertThat(value).isEqualTo(nickname);
    }

    @Test
    void testXSSAttacksCheckWithUriWhitelist() {
        // 测试白名单接口的XSS攻击的安全检查
        toStringMethodArgumentHandler().getProperties().getXssAttack().setCheckEnabled(true);
        toStringMethodArgumentHandler().getProperties().getXssAttack().setServletPathWhitelist(
                new ArrayList<>(Collections.singleton(uriWhitelist)));
        request = new MockHttpServletRequest(null, uriWhitelist);
        Object value = stringMethodArgumentHandler.securityChecks(comment, request, null);
        assertThat(value).isEqualTo(comment);
    }

    @Test
    void testXSSAttacksCheckWithoutUriWhitelist() {
        // 测试非白名单接口的XSS攻击的安全检查（转换实体字符）
        toStringMethodArgumentHandler().getProperties().getXssAttack().setCheckEnabled(true);
        request = new MockHttpServletRequest(null, uri);
        Object value = stringMethodArgumentHandler.securityChecks(comment, request, null);
        assertThat(value).isNotEqualTo(comment);
    }

    @Test
    void testSQLInjectionAttacksCheckWithUriWhitelist() {
        // 测试白名单接口的SQL注入攻击的安全检查
        toStringMethodArgumentHandler().getProperties().getSqlInject().setCheckEnabled(true);
        toStringMethodArgumentHandler().getProperties().getXssAttack().setServletPathWhitelist(
                new ArrayList<>(Collections.singleton(uriWhitelist)));
        request = new MockHttpServletRequest(null, uriWhitelist);
        Object value = stringMethodArgumentHandler.securityChecks(nickname, request, null);
        assertThat(value).isEqualTo(nickname);
    }

    @Test
    void testSQLInjectionAttacksCheckWithoutUriWhitelist() {
        // 测试非白名单接口的SQL注入攻击的安全检查
        toStringMethodArgumentHandler().getProperties().getSqlInject().setCheckEnabled(true);
        request = new MockHttpServletRequest(null, uri);
        assertThatThrownBy(()-> stringMethodArgumentHandler.securityChecks(
                nickname, request, null)).isInstanceOf(SQLKeyboardDetectedException.class);
    }
}
