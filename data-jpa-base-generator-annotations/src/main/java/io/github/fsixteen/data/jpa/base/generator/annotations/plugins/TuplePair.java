package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;

/**
 * 多列 tuple 子查询中的单对列映射声明.
 *
 * <p>
 * 该注解用于把外层查询的一条路径表达式绑定到子查询中的一条路径表达式,
 * 例如 {@code outer.a = sub.c1}. 当前版本主要面向多列 exists/not-exists
 * 语义, 因此 {@link #left()} 与 {@link #right()} 一般都按路径语义使用.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface TuplePair {

    /**
     * 外层查询表达式.
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 子查询表达式.
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.PATH);

}
