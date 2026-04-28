package io.github.fsixteen.data.jpa.base.generator.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.github.fsixteen.data.jpa.base.generator.prepost.FsnPreAuthorize;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class FsnPreAuthorizeAnnotationProcessor implements BeanPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(FsnPreAuthorizeAnnotationProcessor.class);

    private final Map<Class<?>, Map<String, String>> authorityMappings = new HashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = AopUtils.getTargetClass(bean);

        FsnPreAuthorize annotation = AnnotationUtils.findAnnotation(beanClass, FsnPreAuthorize.class);

        if (annotation != null) {
            LOG.info("Processing @FsnPreAuthorize for bean: {}", beanName);
            Map<String, String> methodAuthorities = extractAuthorityExpressions(annotation);
            this.authorityMappings.put(beanClass, methodAuthorities);
            LOG.info("Registered authority mappings for {}: {}", beanClass.getSimpleName(), methodAuthorities);
        }

        return bean;
    }

    private Map<String, String> extractAuthorityExpressions(FsnPreAuthorize annotation) {
        Map<String, String> mappings = new HashMap<>();
        mappings.put("insert", annotation.insert());
        mappings.put("update", annotation.update());
        mappings.put("delete", annotation.delete());
        mappings.put("select", annotation.select());
        mappings.put("selectAll", annotation.selectAll());
        mappings.put("selectOne", annotation.selectOne());
        return mappings;
    }

    public boolean hasPreAuthorizeConfig(Class<?> beanClass, String methodName) {
        return this.authorityMappings.containsKey(beanClass) && this.authorityMappings.get(beanClass).containsKey(methodName);
    }

    public String getAuthorityExpression(Class<?> beanClass, String methodName) {
        Map<String, String> mappings = this.authorityMappings.get(beanClass);
        if (Objects.isNull(mappings)) {
            return "";
        }
        return mappings.getOrDefault(methodName, "");
    }

    public boolean hasPreAuthorizeConfig(Class<?> beanClass) {
        return this.authorityMappings.containsKey(beanClass);
    }

    public Map<String, String> getAuthorityMappings(Class<?> beanClass) {
        return this.authorityMappings.getOrDefault(beanClass, new HashMap<>());
    }

}
