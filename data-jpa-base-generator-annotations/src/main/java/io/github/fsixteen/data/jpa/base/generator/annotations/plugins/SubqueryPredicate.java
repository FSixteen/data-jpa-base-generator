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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SubqueryPredicate.List;

/**
 * canonical 通用子查询注解.
 *
 * <p>
 * 该注解是 compiled 主链路中的统一子查询谓词输入模型, 用于承载
 * {@code in(subquery)}、{@code exists(subquery)}、{@code not exists(subquery)}
 * 三类子查询能力. 它统一描述外层表达式、子查询相关表达式、select 表达式、默认叶子比较、
 * 子查询 where-tree 以及公共选项；`@InTable`、`@Exists`、`@NotExists` 都是它的语义快捷包装.
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
public @interface SubqueryPredicate {

    /**
     * 子查询模式.
     *
     * @return SubqueryMode
     */
    SubqueryMode mode() default SubqueryMode.EXISTS;

    /**
     * 子查询实体.
     *
     * @return Class
     */
    Class<?> targetEntity();

    /**
     * 外层查询的 canonical 表达式.
     *
     * <p>
     * 在 {@code IN} 模式下通常作为外层待匹配值；
     * 在 {@code EXISTS}/{@code NOT_EXISTS} 模式下通常作为外层相关字段.
     * 未显式配置时, 默认回退为当前注解绑定字段对应的实体路径.
     * </p>
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 子查询侧的 canonical 相关表达式.
     *
     * <p>
     * 对于 {@code EXISTS}/{@code NOT_EXISTS}, 该字段通常表示子查询中的相关字段路径；
     * 对于 {@code IN}, 该字段通常与 {@link #select()} 对齐或回退为相同路径.
     * 未显式配置时, 默认回退为当前注解绑定字段对应的实体路径.
     * </p>
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.PATH);

    /**
     * 子查询的 select 表达式.
     *
     * <p>
     * 对于 {@code EXISTS}/{@code NOT_EXISTS}, 未显式配置时通常回退到 {@link #right()}；
     * 对于 {@code IN}, 该字段用于明确子查询返回的匹配值表达式.
     * </p>
     *
     * @return Expr
     */
    Expr select() default @Expr(type = ExprType.AUTO);

    /**
     * 子查询内部默认叶子比较规则.
     *
     * <p>
     * 该字段用于表达子查询 where-tree 中最基础的一条匹配规则,
     * 主要服务于 {@code IN} 模式. 未显式配置时, 会按当前子查询模式回退到对应的默认相关比较.
     * </p>
     *
     * @return Compare
     */
    Compare whereCompare() default @Compare(left = @Expr(type = ExprType.AUTO), right = @Expr(type = ExprType.AUTO));

    /**
     * 子查询内部附加谓词分组树.
     *
     * <p>
     * 该字段用于表达默认叶子比较之外的 and/or 嵌套条件树.
     * 分组中的叶子谓词会以当前注解的 {@link #options()} 作为父级公共选项来源,
     * 再按各自声明的 {@link PredicateOptions} 做覆盖、继承或清空.
     * </p>
     *
     * @return SubqueryGroup
     */
    SubqueryGroup where() default @SubqueryGroup();

    /**
     * 当前子查询谓词的公共选项.
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link SubqueryPredicate} annotations on the same element.
     *
     * @see SubqueryPredicate
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        SubqueryPredicate[] value();

    }

}
