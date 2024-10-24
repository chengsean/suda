package org.suda.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ArgumentResolveBeanFactory配置类
 * @author chengshaozhuang
 * @dateTime 2024-07-12 06:53
 */
@Configuration
public class ArgumentResolverBeanFactoryConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ArgumentResolverBeanFactory argumentResolverBeanFactory() {
        return new ArgumentResolverBeanFactory();
    }

}
