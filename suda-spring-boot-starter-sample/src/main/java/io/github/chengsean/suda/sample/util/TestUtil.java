package io.github.chengsean.suda.sample.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.Objects;

/**
 * @author chengshaozhuang
 */
public abstract class TestUtil {

    private static final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();

    public static String writeValueAsString(Object obj) {
        try {
            return mapper.writeValueAsString(Objects.requireNonNull(obj));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
