package org.suda.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * 该类用于参数解析
 * @author chengshaozhuang
 */
public class ArgumentResolverBeanFactory implements BeanFactoryAware {
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
    protected BeanFactory getInstance() {
        return beanFactory;
    }
}
