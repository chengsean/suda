package org.suda.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * ArgumentResolveBeanFactory配置类
 * @author chengshaozhuang
 * @dateTime 2024-07-12 06:53
 */
@Configuration
@ConditionalOnClass({RequestMappingHandlerAdapter.class})
public class ArgumentResolverBeanFactoryConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ArgumentResolverBeanFactory IBeanFactory() {
        return new ArgumentResolverBeanFactory();
    }

}
