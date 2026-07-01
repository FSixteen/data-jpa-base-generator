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

import io.github.fsixteen.data.jpa.base.generator.annotations.PredicateRole;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NotExists.List;

/**
 * not-exists 子查询快捷注解。
 *
 * <p>
 * 该注解是 {@link SubqueryPredicate} 的 not-exists 语义快捷包装。
 * 语义与 {@link Exists} 相同，但最终生成 {@code not exists (...)}。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@PredicateRole(selection = true)
@SubqueryPredicate(mode = SubqueryMode.NOT_EXISTS, targetEntity = Void.class)
@Inherited
public @interface NotExists {

    /**
     * 子查询实体。
     *
     * @return Class
     */
    Class<?> targetEntity();

    /**
     * 当前 not-exists 子查询的外层 canonical 表达式。
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 当前 not-exists 子查询的子查询侧相关表达式。
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.PATH);

    /**
     * 当前 not-exists 子查询的 select 表达式。
     *
     * @return Expr
     */
    Expr select() default @Expr(type = ExprType.PATH);

    /**
     * 当前 not-exists 子查询的附加谓词分组树。
     *
     * <p>
     * 分组内叶子谓词的 {@link PredicateOptions} 默认也会继承当前注解的 {@link #options()}。
     * </p>
     *
     * @return SubqueryGroup
     */
    SubqueryGroup where() default @SubqueryGroup();

    /**
     * 当前 not-exists 子查询的公共选项。
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link NotExists} annotations on the same element.
     *
     * @see NotExists
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        NotExists[] value();

    }

}
