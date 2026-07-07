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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Length.List;

/**
 * 字符串长度比较快捷注解.
 *
 * <p>
 * 该注解是 {@link Compare} 的长度函数语义快捷包装. 零配置时默认等价于
 * {@code length(inferredTargetPath) = currentFieldValue}；其中
 * {@code inferredTargetPath} 默认优先从当前注解绑定属性名去掉 {@code Length} 后缀推断,
 * 例如 {@code nameLength -> name}；若属性名不满足该约定, 则直接使用当前属性名本身.
 * </p>
 *
 * <p>
 * 默认左表达式为 {@code length(path)}, 默认右表达式为当前注解绑定属性的运行时值；
 * 也可以通过 {@link #left()}、{@link #right()} 和 {@link #extra()} 覆写为完整 canonical
 * 表达式.
 * </p>
 *
 * @author FSixteen
 * @since 1.1.1
 */
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@PredicateRole(selection = true)
@Compare(op = CompareOp.EQ,
    left = @Expr(type = ExprType.FUNCTION, function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH) })))
@Inherited
public @interface Length {

    /**
     * 长度比较操作符.
     *
     * <p>
     * 默认值为 {@link CompareOp#EQ}. 运行期仅支持等值、大小比较、区间比较以及
     * {@code in}/{@code not-in} 这类适用于数值长度结果的操作符.
     * </p>
     *
     * @return CompareOp
     */
    CompareOp op() default CompareOp.EQ;

    /**
     * 长度比较的左侧 canonical 表达式.
     *
     * <p>
     * 未显式配置时, 默认值为 {@code length(path)}. 其中 path 在零配置场景下会按
     * 当前注解绑定属性名推断, 优先去掉 {@code Length} 后缀.
     * </p>
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.FUNCTION,
        function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH) }));

    /**
     * 长度比较的右侧 canonical 表达式.
     *
     * <p>
     * 未显式配置时, 默认取当前注解绑定属性的运行时值.
     * </p>
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.VALUE);

    /**
     * 长度比较的附加 canonical 表达式.
     *
     * <p>
     * 主要用于 {@code between}/{@code not-between} 等需要补充额外操作数的场景.
     * </p>
     *
     * @return Expr[]
     */
    Expr[] extra() default {};

    /**
     * 当前长度比较谓词的公共选项.
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link Length} annotations on the same element.
     */
    @Target({ ANNOTATION_TYPE, FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        Length[] value();

    }

}
