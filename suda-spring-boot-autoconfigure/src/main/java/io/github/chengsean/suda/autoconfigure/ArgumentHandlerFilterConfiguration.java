package io.github.chengsean.suda.autoconfigure;

import io.github.chengsean.suda.core.handler.MethodArgumentHandler;
import io.github.chengsean.suda.core.resolver.request.ArgumentResolverFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author chengshaozhuang
 */
@Configuration
@ConditionalOnBean({MethodArgumentHandler.class})
public class ArgumentHandlerFilterConfiguration {

    private final MethodArgumentHandler stringMethodArgumentHandler;
    private final MethodArgumentHandler fileMethodArgumentHandler;

    public ArgumentHandlerFilterConfiguration(MethodArgumentHandler stringMethodArgumentHandler,
                                              MethodArgumentHandler fileMethodArgumentHandler) {
        this.stringMethodArgumentHandler = stringMethodArgumentHandler;
        this.fileMethodArgumentHandler = fileMethodArgumentHandler;
    }


    @Bean
    @ConditionalOnMissingBean(value = ArgumentResolverFilter.class)
    public ArgumentResolverFilter argumentHandlerFilter() {
        return new ArgumentResolverFilter(stringMethodArgumentHandler, fileMethodArgumentHandler);
    }
}
