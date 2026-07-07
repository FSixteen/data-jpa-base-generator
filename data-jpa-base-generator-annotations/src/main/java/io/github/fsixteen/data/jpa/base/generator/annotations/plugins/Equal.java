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

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal.List;

/**
 * 等值比较快捷注解.
 *
 * <p>
 * 该注解是 {@link Compare} 的等值语义快捷包装. 零配置时默认等价于
 * “当前字段路径 = 当前字段运行时值”；
 * 如需更复杂的比较表达式, 可直接覆写 {@link #left()}、{@link #right()} 和 {@link #options()}.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.0
 */
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@Compare(op = CompareOp.EQ)
@Inherited
public @interface Equal {

    /**
     * 当前等值比较的左侧 canonical 表达式.
     *
     * <p>
     * 未显式配置时, 默认回退为当前注解绑定字段对应的实体路径.
     * </p>
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 当前等值比较的右侧 canonical 表达式.
     *
     * <p>
     * 未显式配置时, 默认回退为当前注解绑定字段的运行时值.
     * </p>
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.VALUE);

    /**
     * 当前等值比较的公共选项.
     *
     * <p>
     * 默认值保持最小化；`default` scope 与默认 group 由 compiled 主链路统一补齐,
     * 或在嵌套场景下从父级上下文继承.
     * </p>
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link Equal} annotations on the same element.
     *
     * @see Equal
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        /**
         * {@link Equal} 集合.
         *
         * @return Equal[]
         */
        Equal[] value();

    }

}
