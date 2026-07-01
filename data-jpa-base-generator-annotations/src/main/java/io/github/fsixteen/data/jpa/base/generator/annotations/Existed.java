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
 * 存在性判断注解的基础元注解。
 *
 * <p>
 * 直接使用时，其默认语义同样回落为 canonical 等值比较；
 * 更常见的用途是作为 existed 语义组合注解的统一元注解入口，
 * 让 compiled 主链路与 {@link Selectable} 共享相同的表达式读取模型。
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
@PredicateRole(existence = true)
@Compare(op = CompareOp.EQ)
@Inherited
public @interface Existed {

    /**
     * canonical 左表达式。
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * canonical 右表达式。
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.VALUE);

    /**
     * canonical 额外操作数。
     *
     * @return Expr[]
     */
    Expr[] extra() default {};

    /**
     * canonical 公共选项。
     *
     * <p>
     * 注解层默认保持空配置，由 compiled 主链路统一补齐默认
     * {@code scope/groups}，避免 existed 家族重复声明同一套基础选项。
     * </p>
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link Existed} annotations on the same element.
     *
     * @see Existed
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        /**
         * {@link Existed} 集合。
         *
         * @return {@link Existed}[]
         */
        Existed[] value();

    }

}
