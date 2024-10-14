package org.suda.handler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.suda.common.Constant;
import org.suda.controller.StringMethodArgumentHandlerTestController;
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
@SpringBootTest(classes = {StringMethodArgumentHandlerTestController.class,
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
    void testRequestParamStringTrimWithNoAnnotation() throws Exception {
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamWithStringTrimWithNoAnnotation";
        String name = "chengshaozhuang    ";
        this.mockMvc.perform(get(url).param("name", name)).andExpect(jsonPath("$.data").value(name.trim()));
    }
}
