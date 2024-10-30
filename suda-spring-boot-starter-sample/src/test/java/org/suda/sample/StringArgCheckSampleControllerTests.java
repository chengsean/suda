package org.suda.sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.suda.common.exception.SQLKeyboardDetectedException;
import org.suda.sample.common.Account;
import org.suda.sample.common.Constant;
import org.suda.sample.common.Result;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
/**
 *
 * @author chengshaozhuang
 */
public class StringArgCheckSampleControllerTests {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private RestTemplate restTemplate;

    private final String host = "http://localhost:8080";

    private final String uriPrefix = host + Constant.STRING_PREFIX_SERVLET_PATH;

    private final String xssKeyword = "<script>alert(\"chengshaozhuang\")</script>";

    private final String sqlKeyword = "Gengshao select * from mysql.user;";

    private final String utf8 = StandardCharsets.UTF_8.name();

    @BeforeEach
    void contextLoads() {
        restTemplate = new RestTemplateBuilder().build();
    }

    @Test
    void testSimpleStringTrimWithRequestParamAnnotation() {
        String uri = uriPrefix + "/simpleStringCheckWithRequestParamAnnotation";
//        UriComponentsBuilder builder = createUriComponentsBuilder(uri, Constant.EMAIL_VALUE);
//        ResponseEntity<Result<Map<String, Object>>> exchange = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
//                null, new ParameterizedTypeReference<Result<Map<String, Object>>>() {});

        HttpEntity<MultiValueMap<String, Object>> httpEntity = createHttpEntity(Constant.EMAIL_VALUE);
        ResponseEntity<Result<Map<String, Object>>> exchange = restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<Result<Map<String, Object>>>() {});
        Result<Map<String, Object>> body = exchange.getBody();
        assertThat(body).isNotNull();
        Map<String, Object> map = body.getData();
        assertThat(map.get(Constant.NAME_KEY)).isEqualTo(Constant.NAME_VALUE.trim());
    }

    @Test
    void testSimpleStringXssKeywordCheckWithRequestParamAnnotation() {
        String uri = uriPrefix + "/simpleStringCheckWithRequestParamAnnotation";
//        UriComponentsBuilder builder = createUriComponentsBuilder(uri, xssKeyword);
//        ResponseEntity<Result<Map<String, Object>>> exchange = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
//                null, new ParameterizedTypeReference<Result<Map<String, Object>>>() {});

        HttpEntity<MultiValueMap<String, Object>> httpEntity = createHttpEntity(xssKeyword);
        ResponseEntity<Result<Map<String, Object>>> exchange = restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<Result<Map<String, Object>>>() {});
        Result<Map<String, Object>> body = exchange.getBody();
        assertThat(body).isNotNull();
        Map<String, Object> map = body.getData();
        assertThat(map.get(Constant.EMAIL_KEY)).isNotEqualTo(xssKeyword);
    }

    @Test
    void testSimpleStringSQLKeywordCheckWithRequestParamAnnotation() {
        String uri = uriPrefix + "/simpleStringCheckWithRequestParamAnnotation";
//        UriComponentsBuilder builder = createUriComponentsBuilder(uri, sqlKeyword);
//        assertThatThrownBy(() -> restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
//                null, new ParameterizedTypeReference<Result<Map<String, Object>>>() {}))
//                .message().contains(SQLKeyboardDetectedException.class.getName());

        HttpEntity<MultiValueMap<String, Object>> httpEntity = createHttpEntity(sqlKeyword);
        assertThatThrownBy(() -> restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<Result<Map<String, Object>>>() {}))
                .message().contains(SQLKeyboardDetectedException.class.getName());
    }

    @Test
    void testMapStringTrimWithoutAnnotation() {
        String uri = uriPrefix + "/mapStringCheckWithRequestParamAnnotation";
//        UriComponentsBuilder builder = createUriComponentsBuilder(uri, Constant.EMAIL_VALUE);
//        new ParameterizedTypeReference<Result<Account>>(){};
//        ResponseEntity<Result<Map<String, Object>>> exchange = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
//                null, new ParameterizedTypeReference<Result<Map<String, Object>>>(){});

        HttpEntity<MultiValueMap<String, Object>> httpEntity = createHttpEntity(Constant.EMAIL_VALUE);
        ResponseEntity<Result<Map<String, Object>>> exchange = restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<Result<Map<String, Object>>>() {});
        Result<Map<String, Object>> body = exchange.getBody();
        assertThat(body).isNotNull();
        Map<String, Object> map = body.getData();
        assertThat(map.get(Constant.EMAIL_KEY)).isEqualTo(Constant.EMAIL_VALUE);
    }

    @Test
    void testMapStringXssKeywordCheckWithRequestParamAnnotation() {
        String uri = uriPrefix + "/mapStringCheckWithRequestParamAnnotation";
//        UriComponentsBuilder builder = createUriComponentsBuilder(uri, xssKeyword);
//        ResponseEntity<Result<Map<String, Object>>> exchange = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
//                null, new ParameterizedTypeReference<Result<Map<String, Object>>>(){});

        HttpEntity<MultiValueMap<String, Object>> httpEntity = createHttpEntity(xssKeyword);
        ResponseEntity<Result<Map<String, Object>>> exchange = restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<Result<Map<String, Object>>>() {});
        Result<Map<String, Object>> body = exchange.getBody();
        assertThat(body).isNotNull();
        Map<String, Object> map = body.getData();
        assertThat(map.get(Constant.EMAIL_KEY)).isNotEqualTo(Constant.EMAIL_VALUE);
    }

    @Test
    void testMapStringSQLKeywordWithRequestParamAnnotation() {
        String uri = uriPrefix + "/mapStringCheckWithRequestParamAnnotation";
//        UriComponentsBuilder builder = createUriComponentsBuilder(uri, sqlKeyword);
//        assertThatThrownBy(() -> restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
//                null, new ParameterizedTypeReference<Result<Map<String, Object>>>(){}))
//                .message().contains(SQLKeyboardDetectedException.class.getName());

        HttpEntity<MultiValueMap<String, Object>> httpEntity = createHttpEntity(sqlKeyword);
        assertThatThrownBy(() -> restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<Result<Map<String, Object>>>() {}))
                .message().contains(SQLKeyboardDetectedException.class.getName());
    }

    @Test
    void testAccountStringTrimWithoutModelAttributeAnnotation() {
        String uri = uriPrefix + "/accountStringCheckWithoutModelAttributeAnnotation";
//        UriComponentsBuilder builder = createUriComponentsBuilder(uri, Constant.EMAIL_VALUE);
//        ResponseEntity<Result<Account>> exchange = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
//                null, new ParameterizedTypeReference<Result<Account>>(){});

        HttpEntity<MultiValueMap<String, Object>> httpEntity = createHttpEntity(Constant.EMAIL_VALUE);
        ResponseEntity<Result<Account>> exchange = restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<Result<Account>>() {});
        Result<Account> body = exchange.getBody();
        assertThat(body).isNotNull();
        Account account = body.getData();
        assertThat(account.getName()).isEqualTo(Constant.NAME_VALUE.trim());
    }

    @Test
    void testAccountStringXssKeywordCheckWithoutModelAttributeAnnotation() {
        String uri = uriPrefix + "/accountStringCheckWithoutModelAttributeAnnotation";
//        UriComponentsBuilder builder = createUriComponentsBuilder(uri, xssKeyword);
//        ResponseEntity<Result<Account>> exchange = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
//                null, new ParameterizedTypeReference<Result<Account>>(){});

        HttpEntity<MultiValueMap<String, Object>> httpEntity = createHttpEntity(xssKeyword);
        ResponseEntity<Result<Account>> exchange = restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<Result<Account>>() {});
        Result<Account> body = exchange.getBody();
        assertThat(body).isNotNull();
        Account account = body.getData();
        assertThat(account.getEmail()).isNotEqualTo(Constant.EMAIL_VALUE);
    }

    @Test
    void testAccountStringSQLKeywordWithoutModelAttributeAnnotation() {
        String uri = uriPrefix + "/accountStringCheckWithoutModelAttributeAnnotation";
//        UriComponentsBuilder builder = createUriComponentsBuilder(uri, sqlKeyword);
//       assertThatThrownBy(() -> restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
//               null, new ParameterizedTypeReference<Result<Account>>(){}))
//               .message().contains(SQLKeyboardDetectedException.class.getName());

        HttpEntity<MultiValueMap<String, Object>> httpEntity = createHttpEntity(sqlKeyword);
        assertThatThrownBy(() -> restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<Result<Account>>() {}))
                .message().contains(SQLKeyboardDetectedException.class.getName());
    }

    private HttpEntity<MultiValueMap<String, Object>> createHttpEntity(String email) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> valueMap = new LinkedMultiValueMap<>();
        valueMap.add("sn", 1L);
        valueMap.add("id", "520032191110242048");
        valueMap.add(Constant.NAME_KEY, Constant.NAME_VALUE);
        valueMap.add(Constant.EMAIL_KEY, email);
        return new HttpEntity<>(valueMap, headers);
    }

    private UriComponentsBuilder createUriComponentsBuilder(String uri, String email) {
        String nameEncode;
        try {
            nameEncode = URLEncoder.encode(Constant.NAME_VALUE, utf8);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            nameEncode = Constant.NAME_VALUE;
        }
        return UriComponentsBuilder
                .fromUriString(uri)
                .queryParam("sn", 1)
                .queryParam("id", 520032191110242048L)
                .queryParam(Constant.NAME_KEY, nameEncode)
                .queryParam(Constant.EMAIL_KEY, email);
    }
}
