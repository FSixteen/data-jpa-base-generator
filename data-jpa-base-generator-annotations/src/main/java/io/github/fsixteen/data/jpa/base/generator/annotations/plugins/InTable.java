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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.InTable.List;

/**
 * 跨表成员判断快捷注解。
 *
 * <p>
 * 该注解是 {@link SubqueryPredicate} 的 in-subquery 语义快捷包装：
 * `left()` 表达外层 source 路径，
 * `right()` 表达子查询 select 路径，
 * `whereCompare()` 表达子查询内部默认叶子比较。
 * 若需要更复杂的子查询内部逻辑，则通过 {@link #where()} 提供完整 where-tree。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.0
 */
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@PredicateRole(selection = true)
@SubqueryPredicate(mode = SubqueryMode.IN, targetEntity = Void.class)
@Inherited
public @interface InTable {

    /**
     * 子查询实体。
     *
     * @return Class
     */
    Class<?> targetEntity();

    /**
     * 当前 in-subquery 比较的外层 canonical 表达式。
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 当前 in-subquery 比较的子查询 select 表达式。
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.PATH);

    /**
     * 当前 in-subquery 子查询的默认叶子比较规则。
     *
     * <p>
     * 该字段是 `InTable` 当前推荐的 canonical 入口，可直接表达
     * 字段对字段、字段对函数、非等值比较等子查询内部匹配条件。
     * 未显式配置时，编译器会自动回退为“外层 source 路径 = 子查询 select 路径”。
     * </p>
     *
     * @return Compare
     */
    Compare whereCompare() default @Compare(left = @Expr(type = ExprType.AUTO), right = @Expr(type = ExprType.AUTO));

    /**
     * 当前 in-subquery 子查询的附加谓词分组树。
     *
     * <p>
     * `whereCompare()` 用于表达默认叶子；本字段用于表达额外的 and/or 嵌套条件树。
     * 分组内叶子谓词的 {@link PredicateOptions} 默认也会继承当前注解的 {@link #options()}。
     * </p>
     *
     * @return SubqueryGroup
     */
    SubqueryGroup where() default @SubqueryGroup();

    /**
     * 当前 in-subquery 比较的公共选项。
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        InTable[] value();

    }

}
