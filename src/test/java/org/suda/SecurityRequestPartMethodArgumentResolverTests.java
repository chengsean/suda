package org.suda;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
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
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link SecurityRequestPartMethodArgumentResolver}请求接口对应的方法参数解析器单元测试
 * @author chengshaozhuang
 */
@SpringBootTest(classes = {
        SecurityRequestPartMethodArgumentResolverTests.TestController.class,
        ArgumentResolverConfiguration.class,
        MockMvcAutoConfiguration.class})
class SecurityRequestPartMethodArgumentResolverTests {

    @Resource
    private MockMvc mockMvc;
    static final String PART_PARAM_NAME = "part";
    static final String MULTIPART_FILE_PARAM_NAME = "file";
    final String fakePdf = "fake-pdf.pdf";
    final String blacklistFile = "blacklist-file.js";
    final String secureFile = "secure-file.txt";

    @Test
    void testRequestParamMultipartFileIllegalFileTypeCheckWithoutRequestPartAnnotation()
            throws ClassloaderUnavailableException, IOException {
        // MultipartFile：测试上传篡改扩展名的文件
        MockPart mockPart = createMockPart(fakePdf, MULTIPART_FILE_PARAM_NAME);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMultipartFileCheckWithoutRequestPartAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(multipart(url)
                .part(mockPart))).message().contains(IllegalFileTypeException.class.getName());
    }

    @Test
    void testRequestParamMultipartFileCheckBlacklistFileCheckWithoutRequestPartAnnotation()
            throws ClassloaderUnavailableException, IOException {
        // MultipartFile：测试上传黑名单的文件
        MockPart mockPart = createMockPart(blacklistFile, MULTIPART_FILE_PARAM_NAME);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMultipartFileCheckWithoutRequestPartAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(multipart(url)
                .part(mockPart))).message().contains(DangerousFileTypeException.class.getName());
    }

    @Test
    void testRequestParamMultipartFileSuccessCheckWithoutRequestPartAnnotation()
            throws ClassloaderUnavailableException, Exception {
        // MultipartFile：测试上传文件成功
        MockPart mockPart = createMockPart(secureFile, MULTIPART_FILE_PARAM_NAME);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamMultipartFileCheckWithoutRequestPartAnnotation";
        this.mockMvc.perform(multipart(url).part(mockPart)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(secureFile));
    }

    @Test
    void testRequestParamPartIllegalFileTypeCheckWithoutRequestPartAnnotation()
            throws ClassloaderUnavailableException, IOException {
        // Part：测试上传篡改扩展名的文件
        MockPart mockPart = createMockPart(fakePdf, PART_PARAM_NAME);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamPartCheckWithoutRequestPartAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(multipart(url)
                .part(mockPart))).message().contains(IllegalFileTypeException.class.getName());
    }

    @Test
    void testRequestParamPartBlacklistFileCheckWithoutRequestPartAnnotation()
            throws ClassloaderUnavailableException, IOException {
        // Part：测试上传黑名单的文件
        MockPart mockPart = createMockPart(blacklistFile, PART_PARAM_NAME);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamPartCheckWithoutRequestPartAnnotation";
        assertThatThrownBy(() -> this.mockMvc.perform(multipart(url)
                .part(mockPart))).message().contains(DangerousFileTypeException.class.getName());
    }

    @Test
    void testRequestParamPartSecureFileCheckWithoutRequestPartAnnotation() throws ClassloaderUnavailableException, Exception {
        // Part：测试上传文件成功
        MockPart mockPart = createMockPart(secureFile, PART_PARAM_NAME);
        String url = Constant.PREFIX_SERVLET_PATH + "/requestParamPartCheckWithoutRequestPartAnnotation";
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

    private static class ClassloaderUnavailableException extends Throwable {
        public ClassloaderUnavailableException(Throwable t) {
            super((t));
        }
    }

    @RestController
    @RequestMapping(Constant.PREFIX_SERVLET_PATH)
    static class TestController {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @RequestMapping(value = "/requestParamMultipartFileCheckWithoutRequestPartAnnotation")
        public Result<?> requestParamMultipartFileCheckWithoutRequestPartAnnotation(MultipartFile file) {
            printLog(file);
            return Result.OK(file.getOriginalFilename());
        }

        @RequestMapping(value = "/requestParamPartCheckWithoutRequestPartAnnotation")
        public Result<?> requestParamPartCheckWithoutRequestPartAnnotation(Part part) {
            printLog(part);
            return Result.OK(part.getSubmittedFileName());
        }

        @RequestMapping(value = "/requestParamMultipartFileCheckWithRequestPartAnnotation")
        public Result<?> requestParamMultipartFileCheckWithRequestPartAnnotation(@RequestPart MultipartFile file) {
            printLog(file);
            return Result.OK(file.getOriginalFilename());
        }

        @RequestMapping(value = "/requestParamPartCheckWithRequestPartAnnotation")
        public Result<?> requestParamPartCheckWithRequestPartAnnotation(@RequestPart Part part) {
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