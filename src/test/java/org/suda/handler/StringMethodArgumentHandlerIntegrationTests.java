package org.suda.handler;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.suda.common.Constant;
import org.suda.common.Result;
import org.suda.config.ArgumentResolverConfiguration;
import org.suda.config.SudaProperties;
import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 字符串安全检查单元测试
 * @author chengshaozhuang
*/
@SpringBootTest(classes = {
        StringMethodArgumentHandlerIntegrationTests.StringMethodArgumentHandlerTestController.class,
        ArgumentResolverConfiguration.class,
        MockMvcAutoConfiguration.class})
class StringMethodArgumentHandlerIntegrationTests {

    @Resource
    private MockMvc mockMvc;

    @Test
    void testRequestParamStringTrimIsEnabledWithNoAnnotation() {
        SudaProperties properties = new SudaProperties();
        properties.getChars().setTrimEnabled(true);
        assertThat(properties.getChars().isTrimEnabled()).isTrue();
    }

    @Test
    void testRequestParamStringTrimWithoutAnnotation() throws Exception {
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamStringTrimWithoutAnnotation";
        testRequestParamStringTrim(url);
    }

    @Test
    void testRequestParamStringTrimByRequestParamAnnotation() throws Exception {
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamStringTrimByRequestParamAnnotation";
        testRequestParamStringTrim(url);
    }


    private void testRequestParamStringTrim(String url) throws Exception {
        String name = "  chengshaozhuang    ";
        mockMvc.perform(get(url).param("name", name)).andExpect(jsonPath("$.data").value(name.trim()));
    }

    @RestController
    @RequestMapping(Constant.PREFIX_SERVLET_PATH)
    static class StringMethodArgumentHandlerTestController {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @RequestMapping(value = "/requestParamStringTrimWithoutAnnotation", method = RequestMethod.GET)
        public Result<?> requestParamStringTrimWithoutAnnotation(String name) {
            printLog(name);
            return Result.OK(name);
        }

        @RequestMapping(value = "/requestParamStringTrimByRequestParamAnnotation", method = RequestMethod.GET)
        public Result<?> requestParamStringTrimByRequestParamAnnotation(@RequestParam String name) {
            printLog(name);
            return Result.OK(name);
        }

        private void printLog(String name) {
            if (name == null) {
                logger.info("入参：null");
            } else {
                logger.info("入参：'{}'，长度：{}", name, name.length());
            }
        }
    }
}
