package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 自定义函数计算.<br>
 *
 * @author FSixteen
 * @since 1.0.2
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface Functions {

    /**
     * 自定义函数.
     * 
     * @return String
     */
    Function[] value() default {};

}
