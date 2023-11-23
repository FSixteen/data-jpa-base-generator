package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 自定义函数计算.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface Function {

    /**
     * 自定义函数名称.
     * 
     * @return String
     */
    String value() default "";

    /**
     * 自定义函数返回值类型, 默认 {@link java.lang.String} .
     * 
     * @return Class
     */
    Class<?> type() default String.class;

    /**
     * 自定义函数传入参数.
     * 
     * @return FunctionArgs[]
     */
    Args[] args() default {};

}
