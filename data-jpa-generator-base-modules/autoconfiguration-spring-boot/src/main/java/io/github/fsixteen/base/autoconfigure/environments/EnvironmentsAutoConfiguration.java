package io.github.fsixteen.base.autoconfigure.environments;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.github.fsixteen.base.autoconfigure.common.CommonAutoConfiguration;

/**
 * Environments Auto Configuration.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@AutoConfiguration(value = "io.github.fsixteen.base.autoconfigure.environments.EnvironmentsAutoConfiguration", after = { CommonAutoConfiguration.class },
    before = { HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class })
@ComponentScan(basePackages = { EnvironmentsAutoConfiguration.BASE_PATH })
@EnableJpaRepositories(basePackages = { EnvironmentsAutoConfiguration.BASE_PATH })
@EntityScan(basePackages = { EnvironmentsAutoConfiguration.DOMAIN_PATH })
@ConditionalOnClass(name = { EnvironmentsAutoConfiguration.CONDITIONAL_CLASS_NAME })
public class EnvironmentsAutoConfiguration {

    public static final String BASE_PATH = "io.github.fsixteen.base.modules.environments";
    public static final String DOMAIN_PATH = "io.github.fsixteen.base.domain.environments";
    public static final String CONDITIONAL_CLASS_NAME = "io.github.fsixteen.base.modules.environments.controller.EnvironmentsInfoController";

}
