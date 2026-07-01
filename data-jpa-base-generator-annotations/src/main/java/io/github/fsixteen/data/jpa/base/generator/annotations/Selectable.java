package io.github.fsixteen.data.jpa.base.generator.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.PredicateOptions;

/**
 * 可查询注解的基础元注解。
 *
 * <p>
 * 直接使用时，其默认语义等价于“当前字段路径 = 当前字段运行时值”。
 * 更常见的用途是作为组合注解的 canonical 元注解载体，让业务注解只声明
 * {@link #left()}、{@link #right()}、{@link #extra()} 与 {@link #options()}，
 * 而把实际谓词构建统一交给 compiled 主链路处理。
 * </p>
 *
 * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal
 * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Unique
 * @author FSixteen
 * @since 1.0.0
 */
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Documented
@PredicateRole(selection = true)
@Compare(op = CompareOp.EQ)
@Inherited
public @interface Selectable {

    /**
     * canonical 左表达式。
     *
     * <p>
     * 默认值直接表示“当前字段路径”。
     * </p>
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * canonical 右表达式。
     *
     * <p>
     * 默认值直接表示“当前字段运行时值”。
     * </p>
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.VALUE);

    /**
     * canonical 额外操作数。
     *
     * <p>
     * 用于 `between` 第二边界值、后续多元比较以及更复杂 canonical meta-annotation 场景。
     * </p>
     *
     * @return Expr[]
     */
    Expr[] extra() default {};

    /**
     * canonical 公共选项。
     *
     * <p>
     * 注解层默认保持空配置，由 compiled 主链路统一补齐默认
     * {@code scope/groups}，避免每个注解再次重复硬编码同一套默认值。
     * </p>
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link Selectable} annotations on the same element.
     *
     * @see Selectable
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        /**
         * {@link Selectable} 集合。
         *
         * @return {@link Selectable}[]
         */
        Selectable[] value();

    }

}
