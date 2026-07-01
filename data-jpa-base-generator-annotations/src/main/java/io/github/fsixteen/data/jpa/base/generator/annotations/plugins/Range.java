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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Range.List;

/**
 * canonical 范围比较注解。
 *
 * <p>
 * 该注解是 compiled 主链路中的统一范围谓词输入模型，用于描述 between / not-between 语义下的
 * 左侧表达式、起始边界、结束边界和公共选项。零配置时默认等价于
 * {@code 当前字段 between 当前字段运行时值[0] and 当前字段运行时值[1]}。
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
@Compare(op = CompareOp.BETWEEN)
@Inherited
public @interface Range {

    /**
     * 范围操作符，仅允许 between / not-between 语义。
     *
     * @return CompareOp
     */
    CompareOp op() default CompareOp.BETWEEN;

    /**
     * 范围比较的左侧 canonical 表达式。
     *
     * <p>
     * 未显式配置时，默认回退为当前注解绑定字段对应的实体路径。
     * </p>
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 范围比较的起始边界表达式。
     *
     * <p>
     * 未显式配置时，默认回退为当前注解绑定字段运行时值中的第一个边界值。
     * </p>
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.VALUE);

    /**
     * 范围比较的后续边界表达式列表。
     *
     * <p>
     * 当前语义下主要使用第一个元素作为结束边界。
     * 未显式配置时，默认回退为当前注解绑定字段运行时值中的第二个边界值。
     * </p>
     *
     * @return Expr[]
     */
    Expr[] extra() default {};

    /**
     * 当前范围谓词的公共选项。
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link Range} annotations on the same element.
     *
     * @see Range
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        Range[] value();

    }

}
