package org.suda.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.suda.handler.StringMethodArgumentHandler;
import org.suda.handler.MethodArgumentHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link MethodArgumentHandler}子类实例配置
 * @author shaozhuang.cheng
 * @dateTime 2024-08-04 12:45
 */
@Configuration
@EnableConfigurationProperties(value = {SudaProperties.class})
public class MethodArgumentHandlerConfig {

    private final SudaProperties properties;

    public MethodArgumentHandlerConfig(SudaProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean(value = StringMethodArgumentHandler.class)
    public MethodArgumentHandler stringMethodArgumentHandler() {
        return new StringMethodArgumentHandler(properties);
    }
}
