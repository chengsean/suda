package io.github.chengsean.suda.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ArgumentResolveBeanFactory配置类
 * @author chengshaozhuang
 */
@Configuration
public class ArgumentResolverBeanFactoryConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ArgumentResolverBeanFactory argumentResolverBeanFactory() {
        return new ArgumentResolverBeanFactory();
    }

}
