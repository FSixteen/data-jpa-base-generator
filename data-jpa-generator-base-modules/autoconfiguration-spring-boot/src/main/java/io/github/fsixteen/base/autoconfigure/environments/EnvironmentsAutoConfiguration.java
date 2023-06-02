package io.github.fsixteen.base.autoconfigure.environments;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.github.fsixteen.base.autoconfigure.common.CommonAutoConfiguration;

/**
 * Environments Auto Configuration.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@AutoConfiguration(before = { CommonAutoConfiguration.class }, beforeName = { "io.github.fsixteen.base.modules.environments.init.InitDatabaseTables" })
@ComponentScan(basePackages = { EnvironmentsAutoConfiguration.BASE_PATH })
@EnableJpaRepositories(basePackages = { EnvironmentsAutoConfiguration.BASE_PATH })
@EntityScan(basePackages = { EnvironmentsAutoConfiguration.DOMAIN_PATH })
@ConditionalOnClass(name = { EnvironmentsAutoConfiguration.CONDITIONAL_CLASS_NAME })
public class EnvironmentsAutoConfiguration implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

    public static final String BASE_PATH = "io.github.fsixteen.base.modules.environments";
    public static final String DOMAIN_PATH = "io.github.fsixteen.base.domain.environments";
    public static final String CONDITIONAL_CLASS_NAME = "io.github.fsixteen.base.modules.environments.controller.EnvironmentsInfoController";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        EntityScanPackages.register(registry, new String[] { BASE_PATH });
        AutoConfigurationPackages.register(registry, new String[] { BASE_PATH });
    }

}
