package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;

/**
 * canonical 深层嵌套函数参数表达式。
 *
 * <p>
 * 这是为规避 Java 注解递归类型限制而引入的第三层函数参数模型。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface DeepNestedExprArg {

    /**
     * 参数表达式类型。
     *
     * @return ExprType
     */
    ExprType type() default ExprType.AUTO;

    /**
     * 固定路径。
     *
     * @return String
     */
    String path() default "";

    /**
     * 运行时值来源字段。
     *
     * @return String
     */
    String valueField() default "";

    /**
     * 固定字面量。
     *
     * @return String
     */
    String literal() default "";

    /**
     * 字面量目标类型。
     *
     * @return Class
     */
    Class<?> javaType() default Object.class;

}
