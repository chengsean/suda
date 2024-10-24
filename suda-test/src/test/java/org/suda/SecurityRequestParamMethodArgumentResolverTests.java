package org.suda;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
import org.suda.resolver.SecurityRequestParamMethodArgumentResolver;

import javax.annotation.Resource;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link SecurityRequestParamMethodArgumentResolver}请求接口对应的方法参数解析器单元测试
 * @author chengshaozhuang
 */
@SpringBootTest(classes = {
        SecurityRequestParamMethodArgumentResolverTests.TestController.class,
        ArgumentResolverConfiguration.class,
        MockMvcAutoConfiguration.class})
class SecurityRequestParamMethodArgumentResolverTests {

    @Resource
    private MockMvc mockMvc;
    static final String NAME_KEY = "name";
    static final String NAME_VALUE = " chengshaozhuang   ";
    static final String ID_KEY = "id";
    static final String COLL_NAME = "collection";
    static final String PART_PARAM_NAME = "part";
    static final String MULTIPART_FILE_PARAM_NAME = "file";
    final String fakePdf = "fake-pdf.pdf";
    final String blacklistFile = "blacklist-file.js";
    final String secureFile = "secure-file.txt";

    @Test
    void testRequestParamStringTrimWithoutRequestParamAnnotation() throws Exception {
        // 测试接口无注解字符串参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamStringTrimWithoutRequestParamAnnotation";
        testRequestParamStringTrim(url);
    }

    @Test
    void testRequestParamStringTrimWithRequestParamAnnotation() throws Exception {
        // 测试接口有'RequestParam'注解字符串参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamStringTrimWithRequestParamAnnotation";
        testRequestParamStringTrim(url);
    }

    private void testRequestParamStringTrim(String url) throws Exception {
        mockMvc.perform(get(url).param(NAME_KEY, NAME_VALUE)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(NAME_VALUE.trim()));
    }

    @Test
    void testRequestParamStringTrimWithRequestParamAnnotationWithName() throws Exception {
        // 测试接口有'RequestParam'注解、注解带有'name'参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamStringTrimWithRequestParamAnnotationWithName";
        final int id = 1024;
        mockMvc.perform(get(url).param(NAME_KEY, NAME_VALUE)
                .param(ID_KEY, Objects.toString(id))).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(NAME_VALUE.trim()))
                .andExpect(jsonPath("$.data.id").value(id));
    }

    @Test
    void testRequestParamCollectionStringTrimWithRequestParamAnnotationWithName() throws Exception {
        // 测试接口有'RequestParam'注解的'Collection<String>'参数去空格是否有效
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamCollectionStringTrimWithRequestParamAnnotationWithName";
        final String value2 = " gengshao  ";
        mockMvc.perform(get(url).param(COLL_NAME, NAME_VALUE, value2)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value(NAME_VALUE.trim()))
                .andExpect(jsonPath("$.data[1]").value(value2.trim()));
    }

    @Test
    void testRequestParamMultipartFileIllegalFileTypeCheckWithoutRequestParamAnnotation()
            throws ClassloaderUnavailableException, IOException {
        // MultipartFile：测试上传篡改扩展名的文件
        MockPart mockPart = createMockPart(fakePdf, MULTIPART_FILE_PARAM_NAME);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMultipartFileCheckWithoutRequestParamAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(MockMvcRequestBuilders.multipart(url)
                .part(mockPart))).message().contains(IllegalFileTypeException.class.getName());
    }

    @Test
    void testRequestParamMultipartFileCheckBlacklistFileCheckWithoutRequestParamAnnotation()
            throws ClassloaderUnavailableException, IOException {
        // MultipartFile：测试上传黑名单的文件
        MockPart mockPart = createMockPart(blacklistFile, MULTIPART_FILE_PARAM_NAME);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMultipartFileCheckWithoutRequestParamAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(MockMvcRequestBuilders.multipart(url)
                .part(mockPart))).message().contains(DangerousFileTypeException.class.getName());
    }

    @Test
    void testRequestParamMultipartFileSuccessCheckWithoutRequestParamAnnotation()
            throws ClassloaderUnavailableException, Exception {
        // MultipartFile：测试上传文件成功
        String filename = secureFile;
        MockPart mockPart = createMockPart(filename, MULTIPART_FILE_PARAM_NAME);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMultipartFileCheckWithoutRequestParamAnnotation";
        this.mockMvc.perform(MockMvcRequestBuilders.multipart(url).part(mockPart)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(filename));
    }

    @Test
    void testRequestParamPartIllegalFileTypeCheckWithoutRequestParamAnnotation()
            throws ClassloaderUnavailableException, IOException {
        // Part：测试上传篡改扩展名的文件
        MockPart mockPart = createMockPart(fakePdf, PART_PARAM_NAME);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamPartCheckWithoutRequestParamAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(MockMvcRequestBuilders.multipart(url)
                .part(mockPart))).message().contains(IllegalFileTypeException.class.getName());
    }

    @Test
    void testRequestParamPartBlacklistFileCheckWithoutRequestParamAnnotation()
            throws ClassloaderUnavailableException, IOException {
        // Part：测试上传黑名单的文件
        MockPart mockPart = createMockPart(blacklistFile, PART_PARAM_NAME);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamPartCheckWithoutRequestParamAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(MockMvcRequestBuilders.multipart(url)
                .part(mockPart))).message().contains(DangerousFileTypeException.class.getName());
    }

    @Test
    void testRequestParamPartSecureFileCheckWithoutRequestParamAnnotation() throws ClassloaderUnavailableException, Exception {
        // Part：测试上传文件成功
        String filename = secureFile;
        MockPart mockPart = createMockPart(filename, PART_PARAM_NAME);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamPartCheckWithoutRequestParamAnnotation";
        this.mockMvc.perform(MockMvcRequestBuilders.multipart(url).part(mockPart)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(filename));
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

    private static class ClassloaderUnavailableException extends Throwable {
        public ClassloaderUnavailableException(Throwable t) {
            super((t));
        }
    }

    @RestController
    @RequestMapping(Constant.PREFIX_SERVLET_PATH)
    static class TestController {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @RequestMapping(value = "/requestParamStringTrimWithoutRequestParamAnnotation", method = RequestMethod.GET)
        public Result<?> requestParamStringTrimWithoutRequestParamAnnotation(String name) {
            printLog(name);
            return Result.OK(name);
        }

        @RequestMapping(value = "/requestParamStringTrimWithRequestParamAnnotation", method = RequestMethod.GET)
        public Result<?> requestParamStringTrimWithRequestParamAnnotation(@RequestParam String name) {
            printLog(name);
            return Result.OK(name);
        }
        @RequestMapping(value = "/requestParamStringTrimWithRequestParamAnnotationWithName", method = RequestMethod.GET)
        public Result<?> requestParamStringTrimWithRequestParamAnnotationWithName(@RequestParam(name = "name") String name,
                                                                                Integer id) {
            printLog(name);
            printLog(id);
            HashMap<String, Object> map = new HashMap<>(5);
            map.put("id", id);
            map.put("name", name);
            return Result.OK(map);
        }

        @RequestMapping(value = "/requestParamCollectionStringTrimWithRequestParamAnnotationWithName",
                method = RequestMethod.GET)
        public Result<?> requestParamCollectionStringTrimWithRequestParamAnnotationWithName(
                @RequestParam(name = COLL_NAME) Collection<String> coll) {
            for (String value : coll) {
                logger.info("Collection value: '{}', String length after trim: {}", value, value.length());
            }
            return Result.OK(coll);
        }

        @RequestMapping(value = "/requestParamMultipartFileCheckWithoutRequestParamAnnotation")
        public Result<?> requestParamMultipartFileCheckWithoutRequestParamAnnotation(MultipartFile file) {
            printLog(file);
            return Result.OK(file.getOriginalFilename());
        }

        @RequestMapping(value = "/requestParamPartCheckWithoutRequestParamAnnotation")
        public Result<?> requestParamPartCheckWithoutRequestParamAnnotation(Part part) {
            printLog(part);
            return Result.OK(part.getSubmittedFileName());
        }

        private void printLog(Object obj) {
            if (obj == null) {
                logger.info("param: null");
            } else if (obj instanceof String) {
                logger.info("param: '{}'，String length after trim: {}", obj, obj.toString().length());
            } else if (obj instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) obj;
                logger.info("file name: '{}'，file length: {}", file.getOriginalFilename(), file.getSize());
            } else if (obj instanceof Part) {
                Part part = (Part) obj;
                logger.info("file name: '{}'，file length: {}", part.getSubmittedFileName(), part.getSize());
            } else {
                logger.info("param: {}", obj);
            }
        }
    }
}