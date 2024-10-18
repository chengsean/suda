package org.suda;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.suda.common.Account;
import org.suda.common.Constant;
import org.suda.common.Result;
import org.suda.common.TestUtil;
import org.suda.config.ArgumentResolverConfiguration;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link SecurityServletModelAttributeMethodProcessor}请求接口对应的方法参数解析器单元测试
 * @author chengshaozhuang
 */
@SpringBootTest(classes = {
        SecurityServletModelAttributeMethodProcessorTests.TestController.class,
        ArgumentResolverConfiguration.class,
        MockMvcAutoConfiguration.class})
public class SecurityServletModelAttributeMethodProcessorTests {

    @Resource
    private MockMvc mockMvc;

    @Test
    void testModelAttributeStringTrimWithoutModelAttributeAnnotation() throws Exception {
        // 测试接口有'ModelAttribute'注解包装对象的字符串参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/modelAttributeStringTrimWithoutModelAttributeAnnotation";
        testModelAttributeStringTrim(url);
    }

    @Test
    void testModelAttributeStringTrimWithModelAttributeAnnotation() throws Exception {
        // 测试接口有'ModelAttribute'注解包装对象的字符串参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/modelAttributeStringTrimWithModelAttributeAnnotation";
        testModelAttributeStringTrim(url);
    }

    private void testModelAttributeStringTrim(String url) throws Exception {
        Account account = new Account();
        account.setSn(1L);
        account.setId("520032191110242048");
        account.setName(" chengshaozhuang   ");
        account.setEmail("520032191110242048@gmail.com");
        String charsetName = StandardCharsets.UTF_8.name();
        String urlEncodedFormData = URLEncoder.encode("sn", charsetName) + "=" + URLEncoder.encode(account.getSn().toString(), charsetName)
                +"&"+
                URLEncoder.encode("id", charsetName) + "=" + URLEncoder.encode(account.getId(), charsetName) +"&"+
                URLEncoder.encode("name", charsetName) + "=" + URLEncoder.encode(account.getName(), charsetName) +"&"+
                URLEncoder.encode("email", charsetName) + "=" + URLEncoder.encode(account.getEmail(), charsetName);
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .content(urlEncodedFormData)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sn").value(account.getSn()))
                .andExpect(jsonPath("$.data.id").value(account.getId()))
                .andExpect(jsonPath("$.data.name").value(account.getName().trim()))
                .andExpect(jsonPath("$.data.email").value(account.getEmail()));
    }

    @RestController
    @RequestMapping(Constant.PREFIX_SERVLET_PATH)
    static class TestController {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @RequestMapping(value = "/modelAttributeStringTrimWithoutModelAttributeAnnotation", method = RequestMethod.GET)
        public Result<?> modelAttributeStringTrimWithoutModelAttributeAnnotation(Account account) {
            printLog(account);
            return Result.OK(account);
        }

        @RequestMapping(value = "/modelAttributeStringTrimWithModelAttributeAnnotation", method = RequestMethod.GET)
        public Result<?> modelAttributeStringTrimWithModelAttributeAnnotation(@ModelAttribute Account account) {
            printLog(account);
            return Result.OK(account);
        }


        private void printLog(Object obj) {
            if (obj == null) {
                logger.info("param: null");
            } else if (obj instanceof String) {
                logger.info("param: '{}'，String length after trim: {}", obj, obj.toString().length());
            } else if (obj instanceof Account) {
                Account account = (Account) obj;
                logger.info("account: '{}'", TestUtil.writeValueAsString(account));
            } else {
                logger.info("param: '{}'", obj);
            }
        }
    }
}
