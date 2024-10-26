package org.suda.core.handler;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.suda.autoconfigure.ArgumentHandlerConfiguration;
import org.suda.common.exception.DangerousFileTypeException;
import org.suda.common.exception.IllegalFileTypeException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

/**
 * 文件安全检查单元测试{@link FileMethodArgumentHandler}
 * @author chengshaozhuang
*/
@SpringBootTest(classes = {ArgumentHandlerConfiguration.class})
class FileMethodArgumentHandlerTests {

    private final String fakePdf = "fake-pdf.pdf";
    private final String blacklistFile = "blacklist-file.js";
    private final String secureFile = "secure-file.txt";
    private static final String PART_PARAM_NAME = "part";
    private static final String MULTIPART_FILE_PARAM_NAME = "file";
    private final String uri = "/index";

    @Resource
    private MethodArgumentHandler fileMethodArgumentHandler;

    private HttpServletRequest request;

    @Test
    void testFileSecurityChecksWithUriWhitelist() throws ClassloaderUnavailableException, IOException {
        // MultipartFile: 测试文件安全检查的Uri白名单
        toFileMethodArgumentHandler().getProperties().getFiles().setCheckEnabled(true);
        String uriWhitelist = "/example";
        toFileMethodArgumentHandler().getProperties().getFiles().setServletPathWhitelist(
                new ArrayList<>(Collections.singleton(uriWhitelist)));
        request = new MockHttpServletRequest(null, uriWhitelist);
        Object obj = createMockPart(fakePdf, MULTIPART_FILE_PARAM_NAME);
        Object result = fileMethodArgumentHandler.securityChecks(obj, request, null);
        assertThat(obj).isEqualTo(result);
    }

    private FileMethodArgumentHandler toFileMethodArgumentHandler() {
        return ((FileMethodArgumentHandler) fileMethodArgumentHandler);
    }

    @Test
    void testFileSecurityChecksDisabled() throws ClassloaderUnavailableException, IOException {
        // Part: 测试文件安全检查的禁用用状态
        toFileMethodArgumentHandler().getProperties().getFiles().setCheckEnabled(false);
        request = new MockHttpServletRequest(null, uri);
        Object obj = createMockPart(fakePdf, PART_PARAM_NAME);
        Object result = fileMethodArgumentHandler.securityChecks(obj, request, null);
        assertThat(obj).isEqualTo(result);
    }

    @Test
    void testMultipartFileCheckIllegalFileType() throws ClassloaderUnavailableException, IOException {
        // MultipartFile：测试上传篡改扩展名的文件
        toFileMethodArgumentHandler().getProperties().getFiles().setCheckEnabled(true);
        request = new MockHttpServletRequest(null, uri);
        Object obj = createMockPart(fakePdf, MULTIPART_FILE_PARAM_NAME);
        assertThatThrownBy(() -> fileMethodArgumentHandler.securityChecks(
                obj, request, null)).isInstanceOf(IllegalFileTypeException.class);
    }

    @Test
    void testMultipartFileCheckBlacklistFile() throws ClassloaderUnavailableException, IOException {
        // MultipartFile：测试上传黑名单文件
        toFileMethodArgumentHandler().getProperties().getFiles().setCheckEnabled(true);
        request = new MockHttpServletRequest(null, uri);
        Object obj = createMockPart(blacklistFile, MULTIPART_FILE_PARAM_NAME);
        assertThatThrownBy(() -> fileMethodArgumentHandler.securityChecks(
                obj, request, null)).isInstanceOf(DangerousFileTypeException.class);
    }

    @Test
    void testMultipartFileCheckSecureFile() throws ClassloaderUnavailableException, IOException {
        // MultipartFile：测试上传安全的文件
        toFileMethodArgumentHandler().getProperties().getFiles().setCheckEnabled(true);
        request = new MockHttpServletRequest(null, uri);
        Object obj = createMockPart(secureFile, MULTIPART_FILE_PARAM_NAME);
        Object result = fileMethodArgumentHandler.securityChecks(obj, request, null);
        assertThat(obj).isEqualTo(result);
    }

    @Test
    void testPartCheckIllegalFileType() throws ClassloaderUnavailableException, IOException {
        // Part：测试上传篡改扩展名的文件
        toFileMethodArgumentHandler().getProperties().getFiles().setCheckEnabled(true);
        request = new MockHttpServletRequest(null, uri);
        Object obj = createMockPart(fakePdf, PART_PARAM_NAME);
        assertThatThrownBy(() -> fileMethodArgumentHandler.securityChecks(
                obj, request, null)).isInstanceOf(IllegalFileTypeException.class);
    }

    @Test
    void testPartCheckBlacklistFile() throws ClassloaderUnavailableException, IOException {
        // Part：测试上传黑名单文件
        toFileMethodArgumentHandler().getProperties().getFiles().setCheckEnabled(true);
        request = new MockHttpServletRequest(null, uri);
        Object obj = createMockPart(blacklistFile, PART_PARAM_NAME);
        assertThatThrownBy(() -> fileMethodArgumentHandler.securityChecks(
                obj, request, null)).isInstanceOf(DangerousFileTypeException.class);
    }

    @Test
    void testPartCheckSecureFile() throws ClassloaderUnavailableException, IOException {
        // Part：测试上传安全的文件
        toFileMethodArgumentHandler().getProperties().getFiles().setCheckEnabled(true);
        request = new MockHttpServletRequest(null, uri);
        Object obj = createMockPart(secureFile, PART_PARAM_NAME);
        Object result = fileMethodArgumentHandler.securityChecks(obj, request, null);
        assertThat(obj).isEqualTo(result);
    }

    private Object createMockPart(String filename, String paramName) throws ClassloaderUnavailableException, IOException {
        ClassLoader classloader = getClassLoader();
        String pathname = Objects.requireNonNull(classloader.getResource(filename)).getFile();
        File file = new File(pathname);
        if (MULTIPART_FILE_PARAM_NAME.equals(paramName)) {
            return new MockMultipartFile(paramName, file.getName(), null, FileUtils.readFileToByteArray(file));
        }
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
}
