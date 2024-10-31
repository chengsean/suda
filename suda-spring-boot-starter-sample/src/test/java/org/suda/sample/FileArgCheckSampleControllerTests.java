package org.suda.sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.suda.common.exception.DangerousFileTypeException;
import org.suda.common.exception.IllegalFileTypeException;
import org.suda.sample.common.Constant;
import org.suda.sample.common.Result;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author chengshaozhuang
 */
public class FileArgCheckSampleControllerTests {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String uriPrefix = Constant.HOST + ":" + Constant.PORT + Constant.FILE_PREFIX_SERVLET_PATH;

    static final String PART_PARAM_NAME = "part";
    static final String MULTIPART_FILE_PARAM_NAME = "file";
    final String fakePdf = "fake-pdf.pdf";
    final String blacklistFile = "blacklist-file.js";
    final String secureFile = "secure-file.txt";

    private RestTemplate restTemplate;


    @BeforeEach
    void contextLoads() {
        restTemplate = new RestTemplateBuilder().build();
        logger.info("RestTemplate instance is created");
    }

    @Test
    void testMultipartFileIllegalFileTypeCheckWithoutRequestParamAnnotation()
            throws ClassloaderUnavailableException, IOException {
        // multipartFile: 测试上传篡改扩展名的文件
        HttpEntity<MultiValueMap<String, Object>> httpEntity = createHttpEntity(fakePdf, MULTIPART_FILE_PARAM_NAME);
        String uri = uriPrefix + "/multipartFileCheckWithoutRequestParamAnnotation";
        assertThatThrownBy(() -> restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<Result<Map<String, Object>>>() {}))
                .message().contains(IllegalFileTypeException.class.getName());
    }

    @Test
    void testMultipartFileBlacklistFileCheckWithoutRequestParamAnnotation()
            throws ClassloaderUnavailableException, IOException {
        // multipartFile: 测试上传黑名单的文件
        HttpEntity<MultiValueMap<String, Object>> httpEntity = createHttpEntity(blacklistFile, MULTIPART_FILE_PARAM_NAME);
        String uri = uriPrefix + "/multipartFileCheckWithoutRequestParamAnnotation";
        assertThatThrownBy(() -> restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<Result<Map<String, Object>>>() {}))
                .message().contains(DangerousFileTypeException.class.getName());
    }

    @Test
    void testMultipartFileSuccessCheckWithoutRequestParamAnnotation()
            throws ClassloaderUnavailableException, Exception {
        // multipartFile: 测试上传文件成功
        HttpEntity<MultiValueMap<String, Object>> httpEntity = createHttpEntity(secureFile, MULTIPART_FILE_PARAM_NAME);
        String uri = uriPrefix + "/multipartFileCheckWithoutRequestParamAnnotation";
        ResponseEntity<Result<String>> entity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<Result<String>>() {});
        Result<String> body = entity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getData()).isEqualTo(secureFile);
    }

    @Test
    void testPartIllegalFileTypeCheckWithoutRequestParamAnnotation()
            throws ClassloaderUnavailableException, IOException {
        // Part：测试上传篡改扩展名的文件
        HttpEntity<MultiValueMap<String, Object>> httpEntity = createHttpEntity(fakePdf, PART_PARAM_NAME);
        String uri = uriPrefix + "/partCheckWithoutRequestParamAnnotation";
        assertThatThrownBy(() -> restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<Result<Map<String, Object>>>() {}))
                .message().contains(IllegalFileTypeException.class.getName());
    }

    @Test
    void testPartBlacklistFileCheckWithoutRequestParamAnnotation()
            throws ClassloaderUnavailableException, IOException {
        // Part：测试上传黑名单的文件
        HttpEntity<MultiValueMap<String, Object>> httpEntity = createHttpEntity(blacklistFile, PART_PARAM_NAME);
        String uri = uriPrefix + "/partCheckWithoutRequestParamAnnotation";
        assertThatThrownBy(() -> restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<Result<Map<String, Object>>>() {}))
                .message().contains(DangerousFileTypeException.class.getName());
    }

    @Test
    void testPartSecureFileCheckWithoutRequestParamAnnotation() throws ClassloaderUnavailableException, Exception {
        // Part：测试上传文件成功
        HttpEntity<MultiValueMap<String, Object>> httpEntity = createHttpEntity(secureFile, PART_PARAM_NAME);
        String uri = uriPrefix + "/partCheckWithoutRequestParamAnnotation";
        ResponseEntity<Result<String>> entity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<Result<String>>() {});
        Result<String> body = entity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getData()).isEqualTo(secureFile);
    }

    private HttpEntity<MultiValueMap<String, Object>> createHttpEntity(String filename, String paramName)
            throws ClassloaderUnavailableException, IOException {
        File file = createFile(filename);
        byte[] data = Files.readAllBytes(file.toPath());
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        ContentDisposition contentDispositionHeader = ContentDisposition.formData()
                .name(paramName)
                .filename(file.getName())
                .build();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDispositionHeader.toString());
        HttpEntity<byte[]> fileEntity = new HttpEntity<>(data, headers);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add(paramName, fileEntity);
        return new HttpEntity<>(body);
    }

    private File createFile(String filename) throws ClassloaderUnavailableException, FileNotFoundException {
        ClassLoader classloader = getClassLoader();
        URL resource = classloader.getResource(filename);
        Objects.requireNonNull(resource, "can't not access file：'"+filename+"' from resource directory");
        File file = new File(resource.getFile());
        if (!file.exists()) {
            throw new FileNotFoundException("File '"+file.getPath()+"' is not exists!");
        }
        if (file.isHidden()) {
            throw new FileNotFoundException("File '"+file.getPath()+"' is hidden!");
        }
        if (file.isDirectory()) {
            throw new FileNotFoundException("File '"+file.getPath()+"' is directory!");
        }
        return file;
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
