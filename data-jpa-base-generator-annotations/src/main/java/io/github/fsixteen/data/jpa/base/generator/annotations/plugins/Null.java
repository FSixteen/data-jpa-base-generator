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

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import io.github.fsixteen.data.jpa.base.generator.annotations.PredicateRole;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Null.List;

/**
 * 布尔驱动空值判断快捷注解.
 *
 * <p>
 * 该注解是布尔驱动的空值判断快捷包装. 零配置时默认作用于当前字段路径,
 * 并根据当前字段布尔值在 {@link #whenTrueUse()} / {@link #whenFalseUse()} 之间切换.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.2
 */
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@PredicateRole(selection = true)
@Inherited
public @interface Null {

    /**
     * 字段值为 true 时使用的空值判断方式.
     *
     * @return ComputerType
     */
    ComputerType whenTrueUse() default ComputerType.IS_NULL;

    /**
     * 字段值为 false 时使用的空值判断方式.
     *
     * @return ComputerType
     */
    ComputerType whenFalseUse() default ComputerType.IS_NOT_NULL;

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
     * Defines several {@link Null} annotations on the same element.
     *
     * @see Null
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        /**
         * 可重复注解容器.
         *
         * @return Null[]
         */
        Null[] value();

    }

    /**
     * 判空表达式类型.
     */
    enum ComputerType {

        /**
         * is-null 判断.
         */
        IS_NULL(Expression::isNull),
        /**
         * is-not-null 判断.
         */
        IS_NOT_NULL(Expression::isNotNull);

        private final Execute execute;

        ComputerType(final Execute execute) {
            this.execute = execute;
        }

        public Execute getExecute() {
            return this.execute;
        }

        public Predicate apply(final Expression<?> expression) {
            return this.execute.apply(expression);
        }

        @FunctionalInterface
        interface Execute {

            Predicate apply(Expression<?> expression);

        }

    }

}
