package org.suda;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.suda.common.Constant;
import org.suda.common.Result;
import org.suda.config.ArgumentResolverConfiguration;
import org.suda.resolver.SecurityPathVariableMethodArgumentResolver;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link SecurityPathVariableMethodArgumentResolver}请求接口对应的方法参数解析器单元测试
 * @author chengshaozhuang
 */
@SpringBootTest(classes = {
        SecurityPathVariableMethodArgumentResolverTests.TestController.class,
        ArgumentResolverConfiguration.class,
        MockMvcAutoConfiguration.class})
class SecurityPathVariableMethodArgumentResolverTests {

    @Resource
    private MockMvc mockMvc;
    static final String _DATE = "2022-11-15";
    static final String _AREA = "earth ";
    static final String _PEOPLE = " 8 billion  ";

    @Test
    void testRequestParamStringTrimWithPathVariableAnnotation() throws Exception {
        // 测试接口有'RequestParam'注解字符串参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/population";
        mockMvc.perform(get(url +"/{date}/{area}/{people}",
                        _DATE, _AREA, _PEOPLE)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.date").value(_DATE))
                .andExpect(jsonPath("$.data.area").value(_AREA.trim()))
                .andExpect(jsonPath("$.data.people").value(_PEOPLE.trim()));
    }

    @RestController
    @RequestMapping(Constant.PREFIX_SERVLET_PATH)
    static class TestController {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @RequestMapping(value = "/population/{date}/{area}/{people}", method = RequestMethod.GET)
        public Result<?> population(@PathVariable String date, @PathVariable String area, @PathVariable String people) {
            logger.info("date: '{}', area: '{}', people: '{}'", date, area, people);
            Map<String, String> map = new LinkedHashMap<>(3);
            map.put("date", date);
            map.put("area", area);
            map.put("people", people);
            return Result.OK(map);
        }
    }
}