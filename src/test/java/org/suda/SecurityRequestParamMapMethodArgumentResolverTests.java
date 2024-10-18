package org.suda;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockPart;
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
import org.suda.exception.DangerousFileTypeException;
import org.suda.exception.IllegalFileTypeException;

import javax.annotation.Resource;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link SecurityRequestParamMapMethodArgumentResolver}请求接口对应的方法参数解析器单元测试
 * @author chengshaozhuang
 */
@SpringBootTest(classes = {
        SecurityRequestParamMapMethodArgumentResolverTests.TestController.class,
        ArgumentResolverConfiguration.class,
        MockMvcAutoConfiguration.class})
class SecurityRequestParamMapMethodArgumentResolverTests {

    @Resource
    private MockMvc mockMvc;
    final String nameKey = "name";
    final String nameValue = " chengshaozhuang   ";
    final String partParamName = "part";
    final String multipartFileParamName = "file";
    final String fakePdf = "fake-pdf.pdf";
    final String blacklistFile = "blacklist-file.js";
    final String secureFile = "secure-file.txt";

    @Test
    void testRequestParamMapStringTrimWithRequestParamAnnotation() throws Exception {
        // 测试接口有'RequestParam'注解的'Map<String,String>'参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMapStringTrimWithRequestParamAnnotation";
        mockMvc.perform(get(url).param(nameKey, nameValue)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(nameValue.trim()));
    }

    @Test
    void testRequestParamMapMultipartFileCheckIllegalFileType() throws ClassloaderUnavailableException, IOException {
        // 测试上传篡改扩展名的文件
        MockPart mockPart = createMockPart(fakePdf, multipartFileParamName);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMapMultipartFileCheckWithRequestParamAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(multipart(url)
                .part(mockPart))).message().contains(IllegalFileTypeException.class.getName());
    }

    @Test
    void testRequestParamMapMultipartFileCheckBlacklistFile() throws ClassloaderUnavailableException, IOException {
        // 测试上传黑名单的文件
        MockPart mockPart = createMockPart(blacklistFile, multipartFileParamName);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMapMultipartFileCheckWithRequestParamAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(multipart(url)
                .part(mockPart))).message().contains(DangerousFileTypeException.class.getName());
    }

    @Test
    void testRequestParamMapMultipartFileCheckSuccess() throws ClassloaderUnavailableException, Exception {
        // 测试上传文件成功
        MockPart mockPart = createMockPart(secureFile, multipartFileParamName);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMapMultipartFileCheckWithRequestParamAnnotation";
        this.mockMvc.perform(multipart(url).part(mockPart)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(secureFile));
    }

    @Test
    void testRequestParamMapPartCheckIllegalFileType() throws ClassloaderUnavailableException, IOException {
        // 测试上传篡改扩展名的文件
        MockPart mockPart = createMockPart(fakePdf, partParamName);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMapPartByRequestParamAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(multipart(url)
                .part(mockPart))).message().contains(IllegalFileTypeException.class.getName());
    }

    @Test
    void testRequestParamMapPartCheckBlacklistFile() throws ClassloaderUnavailableException, IOException {
        // 测试上传黑名单的文件
        MockPart mockPart = createMockPart(blacklistFile, partParamName);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMapPartByRequestParamAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(multipart(url)
                .part(mockPart))).message().contains(DangerousFileTypeException.class.getName());
    }

    @Test
    void testRequestParamMapPartCheckSuccess() throws ClassloaderUnavailableException, Exception {
        // 测试上传文件成功
        MockPart mockPart = createMockPart(secureFile, partParamName);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMapPartByRequestParamAnnotation";
        this.mockMvc.perform(multipart(url).part(mockPart)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(secureFile));
    }

    @Test
    void testRequestParamMultiValueMapStringTrimWithRequestParamAnnotation() throws Exception {
        // 测试接口有'RequestParam'注解的'MultiValueMap<String,String>'参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMultiValueMapStringTrimWithRequestParamAnnotation";
        mockMvc.perform(get(url).param(nameKey, nameValue)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(nameValue.trim()));
    }

    @Test
    void testRequestParamMultiValueMapMultipartFileCheckIllegalFileType() throws ClassloaderUnavailableException, IOException {
        // 测试上传篡改扩展名的文件
        MockPart mockPart = createMockPart(fakePdf, multipartFileParamName);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMultiValueMapMultipartFileCheckWithRequestParamAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(multipart(url)
                .part(mockPart))).message().contains(IllegalFileTypeException.class.getName());
    }

    @Test
    void testRequestParamMultiValueMapMultipartFileCheckBlacklistFile() throws ClassloaderUnavailableException, IOException {
        // 测试上传黑名单的文件
        MockPart mockPart = createMockPart(blacklistFile, multipartFileParamName);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMultiValueMapMultipartFileCheckWithRequestParamAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(multipart(url)
                .part(mockPart))).message().contains(DangerousFileTypeException.class.getName());
    }

    @Test
    void testRequestParamMultiValueMapMultipartFileCheckSuccess() throws ClassloaderUnavailableException, Exception {
        // 测试上传文件成功
        MockPart mockPart = createMockPart(secureFile, multipartFileParamName);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMultiValueMapMultipartFileCheckWithRequestParamAnnotation";
        this.mockMvc.perform(multipart(url).part(mockPart)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(secureFile));
    }

    @Test
    void testRequestParamMultiValueMapPartCheckIllegalFileType() throws ClassloaderUnavailableException, IOException {
        // 测试上传篡改扩展名的文件
        MockPart mockPart = createMockPart(fakePdf, partParamName);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMultiValueMapPartCheckWithRequestParamAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(multipart(url)
                .part(mockPart))).message().contains(IllegalFileTypeException.class.getName());
    }

    @Test
    void testRequestParamMultiValueMapPartCheckBlacklistFile() throws ClassloaderUnavailableException, IOException {
        // 测试上传黑名单的文件
        MockPart mockPart = createMockPart(blacklistFile, partParamName);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMultiValueMapPartCheckWithRequestParamAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(multipart(url)
                .part(mockPart))).message().contains(DangerousFileTypeException.class.getName());
    }

    @Test
    void testRequestParamMultiValueMapPartCheckSuccess() throws ClassloaderUnavailableException, Exception {
        // 测试上传文件成功
        MockPart mockPart = createMockPart(secureFile, partParamName);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMultiValueMapPartCheckWithRequestParamAnnotation";
        this.mockMvc.perform(multipart(url).part(mockPart)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(secureFile));
    }

    private MockPart createMockPart(String filename, String paramName) throws ClassloaderUnavailableException, IOException {
        ClassLoader classloader = getClassLoader();
        String pathname = Objects.requireNonNull(classloader.getResource(filename)).getFile();
        File file = new File(pathname);
        return new MockPart(paramName, file.getName(), FileUtils.readFileToByteArray(file));
    }

    private ClassLoader getClassLoader() throws ClassloaderUnavailableException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        if (classloader == null) {
            classloader = this.getClass().getClassLoader();
        }
        if (classloader == null) {
            try {
                classloader = ClassLoader.getSystemClassLoader();
            } catch (Throwable t) {
                // 无法获取类加载器。。。
                throw new ClassloaderUnavailableException(t);
            }
        }
        return classloader;
    }

    static class ClassloaderUnavailableException extends Throwable {
        public ClassloaderUnavailableException(Throwable t) {
            super((t));
        }
    }

    @RestController
    @RequestMapping(Constant.PREFIX_SERVLET_PATH)
    static class TestController {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @RequestMapping(value = "/requestParamMapStringTrimWithRequestParamAnnotation", method = RequestMethod.GET)
        public Result<?> requestParamMapStringTrimWithRequestParamAnnotation(@RequestParam Map<String, String> map) {
            printLog(map);
            return Result.OK(map);
        }

        @RequestMapping(value = "/requestParamMapMultipartFileCheckWithRequestParamAnnotation")
        public Result<?> requestParamMapMultipartFileCheckWithRequestParamAnnotation(@RequestParam Map<String, MultipartFile> map) {
            printLog(map);
            return Result.OK(new ArrayList<>(map.values()).get(0).getOriginalFilename());
        }

        @RequestMapping(value = "/requestParamMapPartByRequestParamAnnotation")
        public Result<?> requestParamMapPartByRequestParamAnnotation(@RequestParam Map<String, Part> map) {
            printLog(map);
            return Result.OK(new ArrayList<>(map.values()).get(0).getSubmittedFileName());
        }

        @RequestMapping(value = "/requestParamMultiValueMapStringTrimWithRequestParamAnnotation", method = RequestMethod.GET)
        public Result<?> requestParamMultiValueMapStringTrimWithRequestParamAnnotation(
                @RequestParam MultiValueMap<String, String> multiValueMap) {
            printLog(multiValueMap);
            return Result.OK(multiValueMap);
        }

        @RequestMapping(value = "/requestParamMultiValueMapMultipartFileCheckWithRequestParamAnnotation")
        public Result<?> requestParamMultiValueMapMultipartFileCheckWithRequestParamAnnotation(
                @RequestParam MultiValueMap<String, MultipartFile> multiValueMap) {
            printLog(multiValueMap);
            return Result.OK(new ArrayList<>(multiValueMap.values()).get(0).get(0).getOriginalFilename());
        }

        @RequestMapping(value = "/requestParamMultiValueMapPartCheckWithRequestParamAnnotation")
        public Result<?> requestParamMultiValueMapPartByRequestParamAnnotation(
                @RequestParam MultiValueMap<String, Part> multiValueMap) {
            printLog(multiValueMap);
            return Result.OK(new ArrayList<>(multiValueMap.values()).get(0).get(0).getSubmittedFileName());
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