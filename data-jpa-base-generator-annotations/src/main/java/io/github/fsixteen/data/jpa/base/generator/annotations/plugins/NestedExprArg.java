package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;

/**
 * canonical 嵌套函数参数表达式。
 *
 * <p>
 * 配合 {@link NestedExprFunction} 使用，作为函数嵌套的第二层参数声明，避免因 Java 注解不可递归导致的编译错误。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface NestedExprArg {

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

    /**
     * 更深一层的函数。
     *
     * <p>
     * Java 注解元素不允许形成递归类型环，因此 canonical DSL 使用分层函数模型：
     * {@link ExprFunction} -> {@link NestedExprFunction} ->
     * {@link DeepNestedExprFunction}。
     * 这一层用于承接“嵌套函数里的再次嵌套函数”。
     * </p>
     *
     * @return DeepNestedExprFunction
     */
    DeepNestedExprFunction function() default @DeepNestedExprFunction();

}
