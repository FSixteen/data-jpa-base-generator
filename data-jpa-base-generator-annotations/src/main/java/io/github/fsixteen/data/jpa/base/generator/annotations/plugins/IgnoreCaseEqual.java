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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.IgnoreCaseEqual.List;

/**
 * 忽略大小写的等值比较快捷注解.
 *
 * <p>
 * 该注解是 {@link Compare} 的忽略大小写等值语义快捷包装. 零配置时默认等价于：
 * {@code lower(root.get(currentField)) = lower(currentFieldValue)}.
 * 业务仍可覆写 {@link #left()}、{@link #right()} 与 {@link #options()} 来表达更复杂的忽略大小写比较.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@Compare(op = CompareOp.EQ,
    left = @Expr(type = ExprType.FUNCTION, function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.PATH) })),
    right = @Expr(type = ExprType.FUNCTION, function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.VALUE) })))
@Inherited
public @interface IgnoreCaseEqual {

    /**
     * 当前忽略大小写等值比较的左侧 canonical 表达式.
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.FUNCTION,
        function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.PATH) }));

    /**
     * 当前忽略大小写等值比较的右侧 canonical 表达式.
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.FUNCTION,
        function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.VALUE) }));

    /**
     * 当前忽略大小写等值比较的公共选项.
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link IgnoreCaseEqual} annotations on the same element.
     *
     * @see IgnoreCaseEqual
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        /**
         * 可重复注解容器.
         *
         * @return IgnoreCaseEqual[]
         */
        IgnoreCaseEqual[] value();

    }

}
