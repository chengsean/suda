package org.suda.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * BeanFactory配置类
 * @author chengshaozhuang
 * @dateTime 2024-07-12 06:53
 */
@Configuration
@ConditionalOnClass({RequestMappingHandlerAdapter.class})
public class IBeanFactoryConfig {

    @Bean
    @ConditionalOnMissingBean
    public IBeanFactory IBeanFactory() {
        return new IBeanFactory();
    }

    public static class IBeanFactory implements BeanFactoryAware {

        /**
         * 上下文对象实例
         */
        private BeanFactory beanFactory;

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        /**
         * 获取 BeanFactory
         * @return org.springframework.beans.factory.BeanFactory
         */
        public BeanFactory getInstance() {
            return beanFactory;
        }
    }

}
