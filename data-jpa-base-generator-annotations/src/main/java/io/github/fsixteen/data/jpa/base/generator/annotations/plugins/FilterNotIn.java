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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.FilterNotIn.List;

/**
 * 过滤后非成员判断快捷注解.
 *
 * <p>
 * 该注解是 {@link Membership} 的 not-in 语义快捷包装, 并允许在参与谓词前对集合元素做过滤.
 * 零配置时默认等价于“当前字段路径 NOT IN 当前字段运行时集合值”.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.0
 */
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@Membership(op = CompareOp.NOT_IN, collection = @CollectionPolicy())
@Inherited
public @interface FilterNotIn {

    /**
     * 当前成员判断的集合处理策略.
     *
     * @return CollectionPolicy
     */
    CollectionPolicy collection() default @CollectionPolicy();

    /**
     * 当前成员判断的左侧 canonical 表达式.
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 当前成员判断的集合来源表达式.
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.VALUE);

    /**
     * 当前成员判断的公共选项.
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link FilterNotIn} annotations on the same element.
     *
     * @see FilterNotIn
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        /**
         * 可重复注解容器.
         *
         * @return FilterNotIn[]
         */
        FilterNotIn[] value();

    }

}
