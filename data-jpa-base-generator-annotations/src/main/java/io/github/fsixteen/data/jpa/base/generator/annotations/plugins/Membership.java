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
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Membership.List;

/**
 * canonical 成员判断注解.
 *
 * <p>
 * 该注解是 compiled 主链路中的统一成员判断输入模型, 用于表达 in / not-in 语义下的左侧表达式、
 * 成员集合来源表达式、集合处理策略和公共选项. 零配置时默认等价于
 * {@code 当前字段 in 当前字段运行时集合值}.
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
@Compare(op = CompareOp.IN)
@Inherited
public @interface Membership {

    /**
     * 成员判断操作符, 仅允许 in / not-in 语义.
     *
     * @return CompareOp
     */
    CompareOp op() default CompareOp.IN;

    /**
     * 集合值处理策略.
     *
     * @return CollectionPolicy
     */
    CollectionPolicy collection() default @CollectionPolicy();

    /**
     * 成员判断的左侧 canonical 表达式.
     *
     * <p>
     * 未显式配置时, 默认回退为当前注解绑定字段对应的实体路径.
     * </p>
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 成员集合来源表达式.
     *
     * <p>
     * 未显式配置时, 默认回退为当前注解绑定字段的运行时值.
     * 当该值需要按集合策略展开时, 会先应用 {@link #collection()}, 再参与成员判断.
     * </p>
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.VALUE);

    /**
     * 当前成员判断谓词的公共选项.
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link Membership} annotations on the same element.
     *
     * @see Membership
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        Membership[] value();

    }

}
