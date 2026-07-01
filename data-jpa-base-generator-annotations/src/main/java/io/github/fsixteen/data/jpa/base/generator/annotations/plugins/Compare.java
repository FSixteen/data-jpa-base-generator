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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare.List;

/**
 * 通用 canonical 比较注解。
 *
 * <p>
 * 该注解是 compiled 主链路中的统一比较谓词输入模型，用于描述一个比较规则的操作符、左右表达式、
 * 附加操作数与公共选项。零配置时默认等价于“当前字段路径 = 当前字段运行时值”。
 * 其它比较语义例如大于、小于、between、in、like、is-null 等，都通过 {@link #op()} 与相关表达式字段统一表达。
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
public @interface Compare {

    /**
     * 比较操作符。
     *
     * @return CompareOp
     */
    CompareOp op() default CompareOp.EQ;

    /**
     * 集合值处理策略。
     *
     * <p>
     * 当前主要服务于 `IN` / `NOT IN` 等需要结合运行时值展开的比较场景。
     * 非集合类比较通常保持默认空策略即可。
     * </p>
     *
     * @return CollectionPolicy
     */
    CollectionPolicy collection() default @CollectionPolicy();

    /**
     * 当前比较谓词的左侧 canonical 表达式。
     *
     * <p>
     * 该表达式可表示实体路径、运行时值、固定字面量或函数结果。
     * 未显式配置时，默认回退为当前注解绑定字段对应的实体路径。
     * </p>
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 当前比较谓词的右侧 canonical 表达式。
     *
     * <p>
     * 该表达式可表示实体路径、运行时值、固定字面量或函数结果。
     * 未显式配置时，默认回退为当前注解绑定字段的运行时值。
     * </p>
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.VALUE);

    /**
     * 当前比较谓词的附加 canonical 表达式列表。
     *
     * <p>
     * 该字段用于承载除 {@link #left()} 与 {@link #right()} 之外的额外操作数，
     * 顺序参与当前比较语义解释。典型场景包括 {@code BETWEEN} 的第二个边界值。
     * </p>
     *
     * @return Expr[]
     */
    Expr[] extra() default {};

    /**
     * 当前比较谓词的公共选项。
     *
     * <p>
     * 该选项统一承载 scope、groups、required、not、ignoreNull、ignoreEmpty、
     * ignoreBlank、trim 等公共行为，并作为 compiled 主链路中的标准选项输入模型。
     * 未显式配置时，默认值由运行时统一补齐或从父级上下文继承。
     * </p>
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        Compare[] value();

    }

}
