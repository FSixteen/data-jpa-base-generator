package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;

/**
 * canonical 函数参数表达式。
 *
 * <p>
 * 该注解是 {@link ExprFunction} 的参数输入模型，用于声明每个函数参数在 compiled 主链路中的取值来源。
 * 其语义与 {@link Expr} 保持一致，但用于函数参数位置，并通过嵌套函数字段支持多层函数组合。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface ExprArg {

    /**
     * 参数表达式来源类型。
     *
     * <p>
     * 该值决定当前参数按路径、运行时值、固定字面量还是嵌套函数解释。
     * {@code AUTO} 表示交由编译阶段结合所在函数上下文推断。
     * </p>
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
     * <p>
     * 当当前参数按运行时值语义解释时，该字段用于指定从请求参数对象读取值的属性路径。
     * </p>
     *
     * @return String
     */
    String valueField() default "";

    /**
     * 固定字面量文本。
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

    /**
     * 嵌套函数。
     *
     * <p>
     * 当当前参数按函数语义解释时，实际参数值由该嵌套函数表达式提供。
     * </p>
     *
     * @return ExprFunction
     */
    NestedExprFunction function() default @NestedExprFunction();

}
