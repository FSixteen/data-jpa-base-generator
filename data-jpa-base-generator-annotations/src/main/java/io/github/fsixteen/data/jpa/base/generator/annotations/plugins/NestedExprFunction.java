package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * canonical 嵌套函数表达式。
 *
 * <p>
 * 用于 {@link ExprArg} 内部的下一层函数声明。参数通过 {@link #args()} 使用 {@link NestedExprArg}
 * 定义，
 * 形成二层嵌套结构。若需要更深的嵌套，请使用 {@code DeepNestedExprFunction}。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface NestedExprFunction {

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
     * 嵌套函数参数。
     *
     * @return NestedExprArg[]
     */
    NestedExprArg[] args() default {};

}
