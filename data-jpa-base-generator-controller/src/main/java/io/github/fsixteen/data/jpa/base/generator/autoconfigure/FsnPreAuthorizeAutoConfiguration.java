package io.github.fsixteen.data.jpa.base.generator.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.fsixteen.data.jpa.base.generator.aspect.FsnPreAuthorizeAspect;
import io.github.fsixteen.data.jpa.base.generator.security.FsnPreAuthorizeAnnotationProcessor;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = { "org.aspectj.lang.annotation.Aspect", "org.springframework.security.core.Authentication" })
public class FsnPreAuthorizeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FsnPreAuthorizeAnnotationProcessor fsnPreAuthorizeAnnotationProcessor() {
        return new FsnPreAuthorizeAnnotationProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public FsnPreAuthorizeAspect fsnPreAuthorizeAspect(FsnPreAuthorizeAnnotationProcessor annotationProcessor) {
        return new FsnPreAuthorizeAspect(annotationProcessor);
    }

}
