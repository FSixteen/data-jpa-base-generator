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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleExists.List;

/**
 * 多列 tuple exists 子查询快捷注解。
 *
 * <p>
 * 该注解用于表达类似
 * {@code exists(select 1 from T sub where sub.c1 = outer.a and sub.c2 = outer.b ...)}
 * 的多列相关子查询。{@link #pairs()} 负责描述外层与子查询的列映射关系；
 * {@link #whereCompare()} 与 {@link #where()} 用于追加子查询内部过滤条件，
 * 使用方式与 {@link InTable} 的 where 族字段保持一致。
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
@Inherited
public @interface TupleExists {

    /**
     * 子查询实体。
     *
     * @return Class
     */
    Class<?> targetEntity();

    /**
     * 多列映射集合。
     *
     * @return TuplePair[]
     */
    TuplePair[] pairs();

    /**
     * 子查询内部的单个默认附加比较条件。
     *
     * @return Compare
     */
    Compare whereCompare() default @Compare(left = @Expr(type = io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType.AUTO),
        right = @Expr(type = io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType.AUTO));

    /**
     * 子查询内部的附加分组树。
     *
     * @return SubqueryGroup
     */
    SubqueryGroup where() default @SubqueryGroup();

    /**
     * 当前 tuple exists 的公共选项。
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        TupleExists[] value();

    }

}
