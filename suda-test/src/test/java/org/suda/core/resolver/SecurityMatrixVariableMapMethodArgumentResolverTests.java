package org.suda.core.resolver;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.suda.test.Constant;
import org.suda.test.Result;
import org.suda.test.util.TestUtil;
import org.suda.autoconfigure.ArgumentResolverConfiguration;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link SecurityMatrixVariableMapMethodArgumentResolver}请求接口对应的方法参数解析器单元测试
 * @author chengshaozhuang
 */
@SpringBootTest(classes = {
        SecurityMatrixVariableMapMethodArgumentResolverTests.TestController.class,
        ArgumentResolverConfiguration.class,
        MockMvcAutoConfiguration.class})
class SecurityMatrixVariableMapMethodArgumentResolverTests {

    @Resource
    private MockMvc mockMvc;
    static final String _DATE = "2022-11-15";
    static final String _AREA = "earth ";
    static final String _PEOPLE = " 8 billion  ";

    @Test
    void testRequestParamMultiValueMapStringTrimWithMatrixVariableAnnotation() throws Exception {
        // 测试接口有'RequestParam'注解字符串参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/population";
        mockMvc.perform(get(url +"/date/a;date={date}/area/b;area={area}/people/c;people={people}",
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
        public Result<?> population(@MatrixVariable MultiValueMap<String, String> multiValueMap) {
            final Map<String, String> map = new LinkedHashMap<>();
            multiValueMap.forEach((key, values) -> {
                for (String value : values) {
                    map.putIfAbsent(key, value);
                }
            });
            if (!map.isEmpty()) {
                logger.info(TestUtil.writeValueAsString(map));
            }
            return Result.OK(map);
        }
    }
}