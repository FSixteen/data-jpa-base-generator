package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * canonical 深层嵌套函数表达式。
 *
 * <p>
 * 该类型用于承接 {@link NestedExprArg} 的下一层函数，避免 Java 注解元素形成循环定义。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface DeepNestedExprFunction {

    /**
     * 函数名称。
     *
     * @return String
     */
    String name() default "";

    /**
     * 返回值类型。
     *
     * @return Class
     */
    Class<?> type() default Object.class;

    /**
     * 深层函数参数。
     *
     * @return DeepNestedExprArg[]
     */
    DeepNestedExprArg[] args() default {};

}
