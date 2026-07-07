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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.IsNotNull.List;

/**
 * 非空判断快捷注解.
 *
 * <p>
 * 该注解是 {@link NullCheck} 的 is-not-null 语义快捷包装. 零配置时默认作用于当前字段路径.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.2
 */
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@NullCheck(op = CompareOp.IS_NOT_NULL)
@Inherited
public @interface IsNotNull {

    /**
     * 当前空值判断的被判断 canonical 表达式.
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 当前空值判断的公共选项.
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link IsNotNull} annotations on the same element.
     *
     * @see IsNotNull
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        /**
         * 可重复注解容器.
         *
         * @return IsNotNull[]
         */
        IsNotNull[] value();

    }

}
