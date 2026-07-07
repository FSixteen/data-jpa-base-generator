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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Exists.List;

/**
 * exists 子查询快捷注解.
 *
 * <p>
 * 该注解是 {@link SubqueryPredicate} 的 exists 语义快捷包装. 默认语义为：当当前参数字段值为 {@code true}
 * 时,
 * 生成
 * {@code exists(select subRoot.<select> from targetEntity subRoot where subRoot.<right> = root.<left>)}.
 * 若未显式配置表达式, 则 {@code left/right/select} 都默认回退到当前参数字段名. 若配置
 * {@link #where()}, 则其会作为子查询内部的附加谓词组, 与相关字段条件一并进入最终的
 * {@code where} 子句.
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
@SubqueryPredicate(mode = SubqueryMode.EXISTS, targetEntity = Void.class)
@Inherited
public @interface Exists {

    /**
     * 子查询实体.
     *
     * @return Class
     */
    Class<?> targetEntity();

    /**
     * 当前 exists 子查询的外层 canonical 表达式.
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 当前 exists 子查询的子查询侧相关表达式.
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.PATH);

    /**
     * 当前 exists 子查询的 select 表达式.
     *
     * @return Expr
     */
    Expr select() default @Expr(type = ExprType.PATH);

    /**
     * 当前 exists 子查询的附加谓词分组树.
     *
     * <p>
     * 该分组中的路径表达式都以子查询 {@code root} 为基准；若某个叶子注解使用运行时值表达式,
     * 则仍读取当前外层请求参数对象上的字段值. 分组内叶子谓词的
     * {@link PredicateOptions} 默认也会继承当前注解的 {@link #options()}.
     * </p>
     *
     * @return SubqueryGroup
     */
    SubqueryGroup where() default @SubqueryGroup();

    /**
     * 当前 exists 子查询的公共选项.
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link Exists} annotations on the same element.
     *
     * @see Exists
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        Exists[] value();

    }

}
