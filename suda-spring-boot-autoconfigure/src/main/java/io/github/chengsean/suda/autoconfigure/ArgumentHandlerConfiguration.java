package io.github.chengsean.suda.autoconfigure;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import io.github.chengsean.suda.core.handler.ArgumentHandlerProperties;
import io.github.chengsean.suda.core.handler.FileMethodArgumentHandler;
import io.github.chengsean.suda.core.handler.MethodArgumentHandler;
import io.github.chengsean.suda.core.handler.StringMethodArgumentHandler;
import io.github.chengsean.suda.core.tika.TikaWrapper;

/**
 * {@link MethodArgumentHandler}子类实例配置
 * @author chengshaozhuang
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
        return new StringMethodArgumentHandler(cloneProperties());
    }

    @Bean
    @ConditionalOnMissingBean(value = FileMethodArgumentHandler.class)
    public MethodArgumentHandler fileMethodArgumentHandler() {
        return new FileMethodArgumentHandler(cloneProperties(), tikaWrapper());
    }

    private TikaWrapper tikaWrapper() {
        TikaConfig tikaConfig = TikaConfig.getDefaultConfig();
        Metadata metadata = new Metadata();
        return new TikaWrapper(tikaConfig, metadata, cloneProperties());
    }

    private ArgumentHandlerProperties cloneProperties() {
        ArgumentHandlerProperties properties = new ArgumentHandlerProperties();
        BeanUtils.copyProperties(this.properties.getChars(), properties.getChars());
        BeanUtils.copyProperties(this.properties.getXssAttack(), properties.getXssAttack());
        BeanUtils.copyProperties(this.properties.getSqlInject(), properties.getSqlInject());
        BeanUtils.copyProperties(this.properties.getFiles(), properties.getFiles());
        return properties;
    }
}
