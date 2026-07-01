package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;

/**
 * {@link Cases} 的分支命中条件。
 *
 * <p>
 * 该注解用于描述“某个分支何时命中”。compiled 主链路会先对其中的 canonical 表达式做运行时求值，
 * 再按 {@link #op()} 执行比较；若未声明 canonical 条件且需要自定义命中扩展，则通过
 * {@link #predicate()} 接入显式 predicate provider。若需要表达多叶子、多层 AND/OR 组合，
 * 则使用 {@link #group()}。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface CaseWhen {

    /**
     * 自定义条件扩展引用。
     *
     * <p>
     * 只有当当前分支未显式声明 canonical `op/left/right/extra` 时，该扩展入口才会实际参与命中判断。
     * </p>
     *
     * @return PredicateRef
     */
    PredicateRef predicate() default @PredicateRef();

    /**
     * 结构化分支命中分组。
     *
     * <p>
     * 当未声明 canonical {@code left/right/extra} 且未使用 {@link #predicate()} 时，
     * 可通过该字段表达更复杂的 AND/OR 命中树。
     * </p>
     *
     * @return CaseWhenGroup
     */
    CaseWhenGroup group() default @CaseWhenGroup();

    /**
     * canonical 比较操作符。
     *
     * <p>
     * 当显式配置 canonical 表达式字段时，分支命中条件将直接按这里的操作符执行。
     * 若同时未声明 canonical 表达式和 {@link #predicate()}，则该分支默认视为命中。
     * </p>
     *
     * @return CompareOp
     */
    CompareOp op() default CompareOp.EQ;

    /**
     * 分支命中判断的左侧 canonical 表达式。
     *
     * <p>
     * 该表达式基于请求参数对象做运行时求值；路径表达式会读取参数对象属性路径，
     * 运行时值表达式会读取参数对象字段值，函数表达式则在上述结果基础上继续求值。
     * </p>
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.AUTO);

    /**
     * 分支命中判断的右侧 canonical 表达式。
     *
     * <p>
     * 该表达式同样基于请求参数对象做运行时求值，并与 {@link #left()} 一起参与当前分支的命中判断。
     * </p>
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.AUTO);

    /**
     * 分支命中判断的附加 canonical 表达式列表。
     *
     * <p>
     * 该字段用于承载除 {@link #left()} 与 {@link #right()} 之外的额外操作数，
     * 典型场景包括 {@code BETWEEN} 这类需要第二个边界值的命中条件。
     * </p>
     *
     * @return Expr[]
     */
    Expr[] extra() default {};

}
