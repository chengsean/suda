package org.suda;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.suda.common.Constant;
import org.suda.common.Result;
import org.suda.config.ArgumentResolverConfiguration;

import javax.annotation.Resource;
import javax.servlet.http.Part;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * {@link SecurityRequestParamMapMethodArgumentResolver}请求接口对应的方法参数解析器单元测试
 * @author chengshaozhuang
 */
@SpringBootTest(classes = {
        SecurityRequestParamMapMethodArgumentResolverTests
                .SecurityRequestParamMapMethodArgumentResolverTestController.class,
        ArgumentResolverConfiguration.class,
        MockMvcAutoConfiguration.class})
class SecurityRequestParamMapMethodArgumentResolverTests {

    @Resource
    private MockMvc mockMvc;
    final String nameKey = "name";
    final String nameValue = " chengshaozhuang   ";

    @Test
    void testRequestParamMapStringTrimWithRequestParamAnnotation() throws Exception {
        // 测试接口有'RequestParam'注解的'Map<String,String>'参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMapStringTrimWithRequestParamAnnotation";
        mockMvc.perform(get(url).param(nameKey, nameValue))
                .andExpect(jsonPath("$.data.name").value(nameValue.trim()));
    }

    @RestController
    @RequestMapping(Constant.PREFIX_SERVLET_PATH)
    static class SecurityRequestParamMapMethodArgumentResolverTestController {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @RequestMapping(value = "/requestParamMapStringTrimWithRequestParamAnnotation", method = RequestMethod.GET)
        public Result<?> requestParamMapStringTrimWithRequestParamAnnotation(@RequestParam Map<String, String> map) {
            printLog(map);
            return Result.OK(map);
        }

        @SuppressWarnings("unchecked")
        private void printLog(Object obj) {
            if (MultiValueMap.class.isAssignableFrom(obj.getClass())) {
                MultiValueMap<String, Object> multiValueMap = ( MultiValueMap<String, Object>)obj;
                multiValueMap.forEach((key, objects) -> {
                    for (Object value : objects) {
                        if (value == null) {
                            logger.info("MultiValueMap key: '{}'; MultiValueMap value: null", key);
                        } else if (String.class.isAssignableFrom(value.getClass())) {
                            logger.info("MultiValueMap key: '{}'; MultiValueMap value: '{}', String length after trim: {}",
                                    key, value, value.toString().length());
                        } else if (value instanceof MultipartFile) {
                            MultipartFile file = (MultipartFile) value;
                            logger.info("file name: '{}'，file length: {}", file.getOriginalFilename(), file.getSize());
                        } else if (value instanceof Part) {
                            Part part = (Part) value;
                            logger.info("file name: '{}'，file length: {}", part.getSubmittedFileName(), part.getSize());
                        }
                    }
                });
            } else if (Map.class.isAssignableFrom(obj.getClass())) {
                Map<String, Object> stringMap = ((Map<String, Object>)obj);
                for (Map.Entry<String, Object> entry : stringMap.entrySet()) {
                    Object value = entry.getValue();
                    if (value == null) {
                        logger.info("Map key: '{}'; Map value: null", entry.getKey());
                    } else if (String.class.isAssignableFrom(value.getClass())) {
                        logger.info("Map key: '{}'; Map value: '{}', String length after trim: {}",
                                entry.getKey(), entry.getValue(), entry.getValue().toString().length());
                    } else if (value instanceof MultipartFile) {
                        MultipartFile file = (MultipartFile) value;
                        logger.info("file name: '{}'，file length: {}", file.getOriginalFilename(), file.getSize());
                    } else if (value instanceof Part) {
                        Part part = (Part) value;
                        logger.info("file name: '{}'，file length: {}", part.getSubmittedFileName(), part.getSize());
                    }
                }
            }
        }
    }
}