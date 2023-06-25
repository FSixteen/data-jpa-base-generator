package io.github.fsixteen.base.autoconfigure.dictionaries;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.github.fsixteen.base.autoconfigure.common.CommonAutoConfiguration;
import io.github.fsixteen.base.autoconfigure.environments.EnvironmentsAutoConfiguration;

/**
 * Dictionaries Auto Configuration.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@AutoConfiguration(value = "io.github.fsixteen.base.autoconfigure.dictionaries.DictionariesAutoConfiguration",
    after = { CommonAutoConfiguration.class, EnvironmentsAutoConfiguration.class },
    before = { HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class })
@ComponentScan(basePackages = { DictionariesAutoConfiguration.BASE_PATH })
@EnableJpaRepositories(basePackages = { DictionariesAutoConfiguration.BASE_PATH })
@EntityScan(basePackages = { DictionariesAutoConfiguration.DOMAIN_PATH })
@ConditionalOnClass(name = { DictionariesAutoConfiguration.CONDITIONAL_CLASS_NAME })
public class DictionariesAutoConfiguration {

    public static final String BASE_PATH = "io.github.fsixteen.base.modules.dictionaries";
    public static final String DOMAIN_PATH = "io.github.fsixteen.base.domain.dictionaries";
    public static final String CONDITIONAL_CLASS_NAME = "io.github.fsixteen.base.modules.dictionaries.controller.DictionariesInfoController";

}
