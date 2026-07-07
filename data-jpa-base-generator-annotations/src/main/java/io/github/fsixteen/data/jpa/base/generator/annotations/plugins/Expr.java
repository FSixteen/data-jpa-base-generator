package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;

/**
 * canonical 表达式声明.
 *
 * <p>
 * 该注解是注解 DSL 中统一的操作数输入模型, 用于描述一个表达式在 compiled 主链路中的取值来源.
 * 一个 {@link Expr} 可表示实体路径、请求参数对象上的运行时值、固定字面量或函数结果.
 * 上层注解中的 {@code left/right/select} 等字段统一通过它表达.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface Expr {

    /**
     * 表达式来源类型.
     *
     * <p>
     * 该值决定当前表达式应按路径、运行时值、固定字面量还是函数表达式解释.
     * {@code AUTO} 表示交由编译阶段结合所在注解语义做默认推断.
     * </p>
     *
     * @return ExprType
     */
    ExprType type() default ExprType.AUTO;

    /**
     * 固定路径.
     *
     * <p>
     * 当当前表达式按路径语义解释时, 该值表示要访问的属性路径, 例如 {@code user.name}.
     * 在 JPA 路径场景下会被编译为 {@code root.get("user").get("name")} 形式.
     * </p>
     *
     * @return String
     */
    String path() default "";

    /**
     * 运行时值来源字段.
     *
     * <p>
     * 当 {@link #type()} 为 {@code VALUE} 或 {@code VALUE_PATH} 时,
     * 该字段用于指定从请求参数对象哪个属性路径读取值.
     * 未指定时回退到当前注解绑定字段.
     * </p>
     *
     * @return String
     */
    String valueField() default "";

    /**
     * 固定字面量文本.
     *
     * <p>
     * 当当前表达式按字面量语义解释时, 该值会结合 {@link #javaType()} 转换为目标 Java 类型后参与比较或函数调用.
     * </p>
     *
     * @return String
     */
    String literal() default "";

    /**
     * 字面量目标类型.
     *
     * <p>
     * 该值用于声明 {@link #literal()} 的目标 Java 类型, 也可用于辅助函数返回值或显式类型转换的编译.
     * </p>
     *
     * @return Class
     */
    Class<?> javaType() default Object.class;

    /**
     * 函数表达式.
     *
     * <p>
     * 当当前表达式按函数语义解释时, 实际由该字段提供函数名、返回类型和参数列表.
     * </p>
     *
     * @return ExprFunction
     */
    ExprFunction function() default @ExprFunction();

}
