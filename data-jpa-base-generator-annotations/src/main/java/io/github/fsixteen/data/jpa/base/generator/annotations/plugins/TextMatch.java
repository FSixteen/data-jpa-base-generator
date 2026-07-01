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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TextMatch.List;

/**
 * canonical 文本匹配注解。
 *
 * <p>
 * 该注解是 compiled 主链路中的统一文本匹配输入模型，用于表达 like、not-like、
 * starts-with、ends-with 等文本匹配语义下的左右表达式与公共选项。
 * 零配置时默认等价于 {@code 当前字段 like 当前字段运行时值}。
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
@Compare(op = CompareOp.LIKE)
@Inherited
public @interface TextMatch {

    /**
     * 文本匹配操作符，仅允许 like/not-like/starts-with/ends-with 语义。
     *
     * @return CompareOp
     */
    CompareOp op() default CompareOp.LIKE;

    /**
     * 文本匹配的左侧 canonical 表达式。
     *
     * <p>
     * 未显式配置时，默认回退为当前注解绑定字段对应的实体路径。
     * </p>
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 文本匹配目标表达式。
     *
     * <p>
     * 未显式配置时，默认回退为当前注解绑定字段的运行时值。
     * 具体匹配方式由 {@link #op()} 决定。
     * </p>
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.VALUE);

    /**
     * 当前文本匹配谓词的公共选项。
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link TextMatch} annotations on the same element.
     *
     * @see TextMatch
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        TextMatch[] value();

    }

}
