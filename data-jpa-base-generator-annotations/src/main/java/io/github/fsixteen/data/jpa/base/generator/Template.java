package io.github.fsixteen.data.jpa.base.generator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.PredicateOptions;

/**
 * JPA 操作注解类模板.<br>
 * 用于标记可参与查询的注解类, 字段, 方法等.<br>
 *
 * <p>
 * 该模板本身不参与运行时解析，但它是新增注解设计时的约束模板。新注解应优先围绕
 * {@link #left()}、{@link #right()}、{@link #extra()} 与 {@link #options()}
 * 设计。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.2
 */
@Target({})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Template {

    /**
     * canonical 左表达式。
     *
     * <p>
     * 作为新注解设计的一级入口；默认表示“当前字段路径”。
     * </p>
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * canonical 右表达式。
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.VALUE);

    /**
     * canonical 额外操作数。
     *
     * <p>
     * 例如 `between` 的第二个边界值、后续多元函数比较等场景都应优先落在这里。
     * </p>
     *
     * @return Expr[]
     */
    Expr[] extra() default {};

    /**
     * canonical 公共选项。
     *
     * <p>
     * 模板层默认保持空配置，和 compiled 主链路的真实默认规则一致。
     * 新注解若需要默认 scope / groups，应交由 compiled 层统一补齐，而不是继续在注解面复制。
     * </p>
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

}
