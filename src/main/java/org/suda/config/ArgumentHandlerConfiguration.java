package org.suda.config;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.suda.handler.FileMethodArgumentHandler;
import org.suda.handler.StringMethodArgumentHandler;
import org.suda.handler.MethodArgumentHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.suda.util.TikaWrapper;

/**
 * {@link MethodArgumentHandler}子类实例配置
 * @author chengshaozhuang
 * @dateTime 2024-08-04 12:45
 */
@Configuration
@EnableConfigurationProperties(value = {SudaProperties.class})
public class ArgumentHandlerConfiguration {

    private final SudaProperties properties;

    public ArgumentHandlerConfiguration(SudaProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean(value = StringMethodArgumentHandler.class)
    public MethodArgumentHandler stringMethodArgumentHandler() {
        return new StringMethodArgumentHandler(properties);
    }


    @Bean
    @ConditionalOnMissingBean(value = FileMethodArgumentHandler.class)
    public MethodArgumentHandler fileMethodArgumentHandler() {
        return new FileMethodArgumentHandler(properties, tikaWrapper());
    }

    private TikaWrapper tikaWrapper() {
        TikaConfig tikaConfig = TikaConfig.getDefaultConfig();
        Metadata metadata = new Metadata();
        return new TikaWrapper(tikaConfig, metadata, properties);
    }
}
