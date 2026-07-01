package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;

/**
 * {@link Cases} 的显式 else 分支。
 *
 * <p>
 * 该分支只会在前面的 {@link Case} 都未命中时参与执行。
 * 启用后，它沿用与普通分支一致的 {@link CaseThen}、左侧表达式覆写和公共选项覆写模型。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface CaseElse {

    /**
     * 是否启用该 else 分支。
     *
     * @return boolean
     */
    boolean enabled() default false;

    /**
     * else 分支命中后的执行器。
     *
     * @return CaseThen
     */
    CaseThen then() default @CaseThen();

    /**
     * else 分支的左侧 canonical 表达式覆写。
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * else 分支的公共选项覆写。
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

}
