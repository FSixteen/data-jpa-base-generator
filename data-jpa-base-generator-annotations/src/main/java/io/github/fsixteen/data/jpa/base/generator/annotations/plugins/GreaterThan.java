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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.GreaterThan.List;

/**
 * 大于比较快捷注解.
 *
 * <p>
 * 该注解是 {@link Compare} 的大于语义快捷包装. 零配置时默认等价于
 * “当前字段路径 > 当前字段运行时值”.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.0
 */
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@Compare(op = CompareOp.GT)
@Inherited
public @interface GreaterThan {

    /**
     * 当前大于比较的左侧 canonical 表达式.
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 当前大于比较的右侧 canonical 表达式.
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.VALUE);

    /**
     * 当前大于比较的公共选项.
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link GreaterThan} annotations on the same element.
     *
     * @see GreaterThan
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        /**
         * 可重复注解容器.
         *
         * @return GreaterThan[]
         */
        GreaterThan[] value();

    }

}
