package org.suda;

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
import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * description
 *
 * @author chengshaozhuang
 */
@SpringBootTest(classes = {
        SecurityRequestParamMethodArgumentResolverTests
                .SecurityRequestParamMethodArgumentResolverTestController.class,
        ArgumentResolverConfiguration.class,
        MockMvcAutoConfiguration.class})
class SecurityRequestParamMethodArgumentResolverTests {

    @Resource
    private MockMvc mockMvc;
    static final String NAME_KEY = "name";
    static final String NAME_VALUE = " chengshaozhuang   ";
    static final String ID_KEY = "id";
    static final String COLL_NAME = "collection";

    @Test
    void testRequestParamStringTrimWithoutAnnotation() throws Exception {
        // 测试接口无注解字符串参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamStringTrimWithoutAnnotation";
        testRequestParamStringTrim(url);
    }

    @Test
    void testRequestParamStringTrimByRequestParamAnnotation() throws Exception {
        // 测试接口有'RequestParam'注解字符串参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamStringTrimByRequestParamAnnotation";
        testRequestParamStringTrim(url);
    }

    @Test
    void testRequestParamStringTrimByRequestParamAnnotationWithName() throws Exception {
        // 测试接口有'RequestParam'注解、注解带有'name'参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamStringTrimByRequestParamAnnotationWithName";
        final int id = 1024;
        mockMvc.perform(get(url).param(NAME_KEY, NAME_VALUE)
                .param(ID_KEY, Objects.toString(id))).andExpect(jsonPath("$.data.name").value(NAME_VALUE.trim()))
                .andExpect(jsonPath("$.data.id").value(id));
    }

    private void testRequestParamStringTrim(String url) throws Exception {
        mockMvc.perform(get(url).param(NAME_KEY, NAME_VALUE)).andExpect(jsonPath("$.data").value(NAME_VALUE.trim()));
    }

    @Test
    void testRequestParamCollectionStringTrimByRequestParamAnnotation() throws Exception {
        // 测试接口有'RequestParam'注解的'MultiValueMap<String,String>'参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamCollectionStringTrimByRequestParamAnnotation";
        final String value2 = " gengshao  ";
        mockMvc.perform(get(url).param(COLL_NAME, NAME_VALUE, value2))
                .andExpect(jsonPath("$.data[0]").value(NAME_VALUE.trim()))
                .andExpect(jsonPath("$.data[1]").value(value2.trim()));
    }

    @RestController
    @RequestMapping(Constant.PREFIX_SERVLET_PATH)
    static class SecurityRequestParamMethodArgumentResolverTestController {

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

        private void printLog(Object obj) {
            if (obj == null) {
                logger.info("param: null");
            } else if (obj instanceof String) {
                logger.info("param: '{}'，String length after trim: {}", obj, obj.toString().length());
            } else {
                logger.info("param: {}", obj);
            }
        }

        @RequestMapping(value = "/requestParamStringTrimByRequestParamAnnotationWithName", method = RequestMethod.GET)
        public Result<?> requestParamStringTrimByRequestParamAnnotationWithName(@RequestParam(name = "name") String name,
                                                                                Integer id) {
            printLog(name);
            printLog(id);
            HashMap<String, Object> map = new HashMap<>(5);
            map.put("id", id);
            map.put("name", name);
            return Result.OK(map);
        }

        @RequestMapping(value = "/requestParamCollectionStringTrimByRequestParamAnnotation", method = RequestMethod.GET)
        public Result<?> requestParamCollectionStringTrimByRequestParamAnnotation(@RequestParam(name = COLL_NAME) Collection<Object> coll) {
            for (Object value : coll) {
                logger.info("Collection value: '{}', String length after trim: {}", value, value.toString().length());
            }
            return Result.OK(coll);
        }
    }
}