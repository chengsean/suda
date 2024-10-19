package org.suda.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.KotlinDetector;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.annotation.ErrorsMethodArgumentResolver;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.MapMethodProcessor;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.annotation.RequestHeaderMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.SessionStatusMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.ContinuationHandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewRequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMapMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RedirectAttributesMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestAttributeMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletResponseMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.SessionAttributeMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.UriComponentsBuilderMethodArgumentResolver;
import org.suda.SecurityMatrixVariableMapMethodArgumentResolver;
import org.suda.SecurityMatrixVariableMethodArgumentResolver;
import org.suda.SecurityPathVariableMethodArgumentResolver;
import org.suda.SecurityRequestParamMapMethodArgumentResolver;
import org.suda.SecurityRequestParamMethodArgumentResolver;
import org.suda.SecurityRequestPartMethodArgumentResolver;
import org.suda.SecurityRequestResponseBodyMethodProcessor;
import org.suda.SecurityServletModelAttributeMethodProcessor;
import org.suda.handler.MethodArgumentHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 参数安全检查配置
 * @author chengshaozhuang
 * @dateTime 2023-08-06 01:41
 */
@Configuration
@Import({WebMvcAutoConfiguration.class, ArgumentResolverBeanFactoryConfiguration.class, ArgumentHandlerConfiguration.class})
public class ArgumentResolverConfiguration {

    /**
     *检查是否存在Jackson组件
     */
    private static final boolean jackson2Present;
    static {
        ClassLoader classLoader = WebMvcConfigurationSupport.class.getClassLoader();
        jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) &&
                ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader);
    }
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<Object> requestResponseBodyAdvice = new ArrayList<>();
    private final RequestMappingHandlerAdapter adapter;
    private final ArgumentResolverBeanFactory argumentResolverBeanFactory;
    private final MethodArgumentHandler stringMethodArgumentHandler;
    private final MethodArgumentHandler fileMethodArgumentHandler;


    public ArgumentResolverConfiguration(RequestMappingHandlerAdapter adapter, ArgumentResolverBeanFactory argumentResolverBeanFactory,
                                         MethodArgumentHandler stringMethodArgumentHandler, MethodArgumentHandler fileMethodArgumentHandler) {
        this.adapter = adapter;
        this.argumentResolverBeanFactory = argumentResolverBeanFactory;
        this.stringMethodArgumentHandler = stringMethodArgumentHandler;
        this.fileMethodArgumentHandler = fileMethodArgumentHandler;
        setNewArgumentResolvers();
    }

    private void setNewArgumentResolvers() {
        initRequestResponseBodyAdvice();
        adapter.setArgumentResolvers(createArgumentResolvers());
        logger.info("already update the ArgumentResolvers");
    }

    /**
     * 在已有的参数处理类上进行扩展
     * @author chengshaozhuang
     * @return java.util.List<org.springframework.web.method.support.HandlerMethodArgumentResolver>
    */
    private List<HandlerMethodArgumentResolver> createArgumentResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>(30);

        // Annotation-based argument resolution
        resolvers.add(new SecurityRequestParamMethodArgumentResolver(getBeanFactory(), false, stringMethodArgumentHandler,
                fileMethodArgumentHandler));
        resolvers.add(new SecurityRequestParamMapMethodArgumentResolver(stringMethodArgumentHandler, fileMethodArgumentHandler));
        resolvers.add(new SecurityPathVariableMethodArgumentResolver(stringMethodArgumentHandler));
        resolvers.add(new PathVariableMapMethodArgumentResolver());
        // 功能暂不支持(Not at this time)
//        resolvers.add(new SecurityPathVariableMapMethodArgumentResolver(stringMethodArgumentHandler));
        resolvers.add(new SecurityMatrixVariableMethodArgumentResolver(stringMethodArgumentHandler));
        resolvers.add(new SecurityMatrixVariableMapMethodArgumentResolver(stringMethodArgumentHandler));
        resolvers.add(new SecurityServletModelAttributeMethodProcessor(false, stringMethodArgumentHandler));
        resolvers.add(new SecurityRequestResponseBodyMethodProcessor(adapter.getMessageConverters(),
                requestResponseBodyAdvice, stringMethodArgumentHandler));
        resolvers.add(new SecurityRequestPartMethodArgumentResolver(adapter.getMessageConverters(), requestResponseBodyAdvice,
                fileMethodArgumentHandler));
        resolvers.add(new RequestHeaderMethodArgumentResolver(getBeanFactory()));
        resolvers.add(new RequestHeaderMapMethodArgumentResolver());
        resolvers.add(new ServletCookieValueMethodArgumentResolver(getBeanFactory()));
        resolvers.add(new ExpressionValueMethodArgumentResolver(getBeanFactory()));
        resolvers.add(new SessionAttributeMethodArgumentResolver());
        resolvers.add(new RequestAttributeMethodArgumentResolver());

        // Type-based argument resolution
        resolvers.add(new ServletRequestMethodArgumentResolver());
        resolvers.add(new ServletResponseMethodArgumentResolver());
        resolvers.add(new HttpEntityMethodProcessor(adapter.getMessageConverters(), requestResponseBodyAdvice));
        resolvers.add(new RedirectAttributesMethodArgumentResolver());
        resolvers.add(new ModelMethodProcessor());
        resolvers.add(new MapMethodProcessor());
        resolvers.add(new ErrorsMethodArgumentResolver());
        resolvers.add(new SessionStatusMethodArgumentResolver());
        resolvers.add(new UriComponentsBuilderMethodArgumentResolver());
        if (KotlinDetector.isKotlinPresent()) {
            resolvers.add(new ContinuationHandlerMethodArgumentResolver());
        }

        // Custom arguments
        if (adapter.getCustomArgumentResolvers() != null) {
            resolvers.addAll(adapter.getCustomArgumentResolvers());
        }

        // Catch-all
        resolvers.add(new PrincipalMethodArgumentResolver());
        resolvers.add(new SecurityRequestParamMethodArgumentResolver(getBeanFactory(), true, stringMethodArgumentHandler,
                fileMethodArgumentHandler));
        resolvers.add(new SecurityServletModelAttributeMethodProcessor(true, stringMethodArgumentHandler));

        return resolvers;
    }

    private void initRequestResponseBodyAdvice() {
        if (jackson2Present) {
            requestResponseBodyAdvice.add(new JsonViewRequestBodyAdvice());
            requestResponseBodyAdvice.add(new JsonViewResponseBodyAdvice());
        }
    }

    @Nullable
    private ConfigurableBeanFactory getBeanFactory() {
        BeanFactory beanFactory = argumentResolverBeanFactory.getInstance();
        if (beanFactory instanceof ConfigurableBeanFactory) {
            return (ConfigurableBeanFactory) beanFactory;
        }
        logger.warn("instance of '"+ConfigurableBeanFactory.class.getName()+"' not found!");
        return null;
    }

}
