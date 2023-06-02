package io.github.fsixteen.base.autoconfigure.dictionaries;

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
import io.github.fsixteen.base.autoconfigure.environments.EnvironmentsAutoConfiguration;

/**
 * Dictionaries Auto Configuration.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@AutoConfiguration(before = { CommonAutoConfiguration.class, EnvironmentsAutoConfiguration.class },
    beforeName = { "io.github.fsixteen.base.modules.dictionaries.init.InitDatabaseTables" })
@ComponentScan(basePackages = { DictionariesAutoConfiguration.BASE_PATH })
@EnableJpaRepositories(basePackages = { DictionariesAutoConfiguration.BASE_PATH })
@EntityScan(basePackages = { DictionariesAutoConfiguration.DOMAIN_PATH })
@ConditionalOnClass(name = { DictionariesAutoConfiguration.CONDITIONAL_CLASS_NAME })
public class DictionariesAutoConfiguration implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

    public static final String BASE_PATH = "io.github.fsixteen.base.modules.dictionaries";
    public static final String DOMAIN_PATH = "io.github.fsixteen.base.domain.dictionaries";
    public static final String CONDITIONAL_CLASS_NAME = "io.github.fsixteen.base.modules.dictionaries.controller.DictionariesInfoController";

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
