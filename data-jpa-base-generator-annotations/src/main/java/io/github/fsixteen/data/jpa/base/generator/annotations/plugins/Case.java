package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;

/**
 * {@link Cases} 的单个分支定义.
 *
 * <p>
 * 一个分支由命中条件 {@link #when()}、命中后的执行规则 {@link #then()}、可选的左侧表达式覆写
 * {@link #left()} 以及分支级公共选项覆写 {@link #options()} 组成.
 * 它是 {@link Cases} 分支树中的最小业务单元.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
@Documented
public @interface Case {

    /**
     * 分支命中条件.
     * 
     * @return CaseWhen
     */
    CaseWhen when();

    /**
     * 分支命中后的执行器.
     *
     * <p>
     * 未配置显式 processor 且未声明 canonical then 规则时, 运行时回退到内建默认分支语义,
     * 直接生成“分支字段 = 当前字段值”的 canonical 等值谓词.
     * </p>
     * 
     * @return CaseThen
     */
    CaseThen then() default @CaseThen();

    /**
     * 当前分支的左侧 canonical 表达式覆写.
     *
     * <p>
     * 当当前分支命中后, 若分支内部 canonical then 规则需要一个左侧目标,
     * 则优先使用该表达式；未显式配置时回退到外层 {@link Cases#left()}.
     * </p>
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 当前分支的公共选项覆写.
     *
     * <p>
     * 未显式配置时继承外层 {@link Cases#options()}, 可用于按分支粒度覆盖 scope、groups、
     * required、not、ignoreNull、ignoreEmpty、ignoreBlank、trim 等公共行为.
     * </p>
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

}
