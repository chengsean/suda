package org.suda;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.suda.common.Constant;
import org.suda.common.Result;
import org.suda.config.ArgumentResolverConfiguration;

import javax.annotation.Resource;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link SecurityMatrixVariableMethodArgumentResolver}请求接口对应的方法参数解析器单元测试
 * @author chengshaozhuang
 */
@SpringBootTest(classes = {
        SecurityMatrixVariableMethodArgumentResolverTests.TestController.class,
        ArgumentResolverConfiguration.class,
        MockMvcAutoConfiguration.class})
class SecurityMatrixVariableMethodArgumentResolverTests {

    @Resource
    private MockMvc mockMvc;
    static final String _DATE = "2022-11-15";
    static final String _AREA = "earth ";
    static final String _PEOPLE = " 8 billion  ";

    @Test
    void testRequestParamStringTrimWithMatrixVariableAnnotation() throws Exception {
        // 测试接口有'RequestParam'注解字符串参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/population";
        mockMvc.perform(get(url +"/date/date;p={date}/area/area;p={area}/people/people;p={people}",
                        _DATE, _AREA, _PEOPLE)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.date").value(_DATE))
                .andExpect(jsonPath("$.data.area").value(_AREA.trim()))
                .andExpect(jsonPath("$.data.people").value(_PEOPLE.trim()));
    }

    @RestController
    @RequestMapping(Constant.PREFIX_SERVLET_PATH)
    static class TestController {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @RequestMapping(value = "/population/date/{date}/area/{area}/people/{people}", method = RequestMethod.GET)
        public Result<?> population(@MatrixVariable(name="p", pathVar="date") String p1,
                                    @MatrixVariable(name="p", pathVar="area") String p2,
                                    @MatrixVariable(name="p", pathVar="people") String p3) {
            logger.info("date: '{}', area: '{}', people: '{}'", p1, p2, p3);
            Map<String, String> map = new LinkedHashMap<>(3);
            map.put("date", p1);
            map.put("area", p2);
            map.put("people", p3);
            return Result.OK(map);
        }
    }
}