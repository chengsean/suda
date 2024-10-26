package org.suda.core.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.suda.test.Account;
import org.suda.test.Constant;
import org.suda.test.Result;
import org.suda.test.util.TestUtil;
import org.suda.autoconfigure.ArgumentResolverConfiguration;

import javax.annotation.Resource;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link SecurityRequestResponseBodyMethodProcessor}请求接口对应的方法参数解析器单元测试
 * @author chengshaozhuang
 */
@SpringBootTest(classes = {
        SecurityRequestResponseBodyMethodProcessorTests.TestController.class,
        ArgumentResolverConfiguration.class,
        MockMvcAutoConfiguration.class})
class SecurityRequestResponseBodyMethodProcessorTests {

    @Resource
    private MockMvc mockMvc;

    @Test
    void testRequestBodyStringTrimWithRequestBodyAnnotation() throws Exception {
        // 测试接口有'RequestBody'包装对象的字符串参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/requestBodyStringTrimWithRequestBodyAnnotation";
        testRequestBodyStringTrimWith(url);
    }

    private void testRequestBodyStringTrimWith(String url) throws Exception {
        Account account = new Account();
        account.setSn(1L);
        account.setId("520032191110242048");
        account.setName(" chengshaozhuang   ");
        account.setEmail("520032191110242048@gmail.com");
        account.setBirthday(LocalDate.parse("1911-10-24"));
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        String content = objectMapper.writeValueAsString(account);
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)
                .content(content)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sn").value(account.getSn()))
                .andExpect(jsonPath("$.data.id").value(account.getId()))
                .andExpect(jsonPath("$.data.name").value(account.getName().trim()))
                .andExpect(jsonPath("$.data.email").value(account.getEmail()))
                .andExpect(jsonPath("$.data.birthday").value(account.getBirthday().toString()));
    }


    @RestController
    @RequestMapping(Constant.PREFIX_SERVLET_PATH)
    static class TestController {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @RequestMapping(value = "/requestBodyStringTrimWithRequestBodyAnnotation", method = RequestMethod.GET)
        @ResponseBody
        public Result<?> requestBodyStringTrimWithRequestBodyAnnotation(@RequestBody Account account) {
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