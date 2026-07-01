package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NotBetween.List;

/**
 * 非范围比较快捷注解。
 *
 * <p>
 * 该注解是 {@link Range} 的 not-between 语义快捷包装。零配置时默认等价于
 * “当前字段路径 NOT BETWEEN 当前字段运行时范围值[0] AND 当前字段运行时范围值[1]”。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.0
 */
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@Range(op = CompareOp.NOT_BETWEEN)
@Inherited
public @interface NotBetween {

    /**
     * 当前范围比较的左侧 canonical 表达式。
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 当前范围比较的起始边界表达式。
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.VALUE);

    /**
     * 当前范围比较的后续边界表达式列表。
     *
     * @return Expr[]
     */
    Expr[] extra() default {};

    /**
     * 当前范围比较的公共选项。
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link NotBetween} annotations on the same element.
     *
     * @see NotBetween
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        /**
         * 可重复注解容器。
         *
         * @return NotBetween[]
         */
        NotBetween[] value();

    }

}
