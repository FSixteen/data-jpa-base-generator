package io.github.fsixteen.base.autoconfigure.configurations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.DependsOn;

/**
 * Depends On Configurations Initialization.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DependsOn(value = { "io.github.fsixteen.base.modules.configurations.init.InitDatabaseTables" })
public @interface DependsOnConfigurationsInitialization {

}
