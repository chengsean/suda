package org.suda.config;

import org.suda.handler.StringMethodArgumentHandler;
import org.suda.handler.MethodArgumentHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link MethodArgumentHandler}子类实例配置
 * @author chengshaozhuang
 * @dateTime 2024-08-04 12:45
 */
@Configuration
public class MethodArgumentHandlerConfig {

    @Bean
    @ConditionalOnMissingBean(value = StringMethodArgumentHandler.class)
    public MethodArgumentHandler stringMethodArgumentHandler() {
        return new StringMethodArgumentHandler(null);
    }
}
