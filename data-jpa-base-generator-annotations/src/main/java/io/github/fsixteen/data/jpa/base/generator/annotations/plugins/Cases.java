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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Cases.List;

/**
 * 条件分支注解.
 *
 * <p>
 * 该注解用于按请求参数对象的运行时状态, 在多个 {@link Case} 分支中选择一个生成最终的 JPA
 * {@code Predicate}. 它是 compiled 主链路中的专用分支输入模型：
 * {@link CaseWhen} 负责描述分支命中条件, {@link CaseThen} 负责描述命中后的谓词产出规则,
 * 而 {@link #left()} 与 {@link #options()} 则提供分支级默认作用表达式和公共选项来源.
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
public @interface Cases {

    /**
     * 所有可选分支.
     *
     * @return Case[]
     */
    Case[] value();

    /**
     * 显式 else 分支.
     *
     * <p>
     * 当前面的 {@link #value()} 分支都未命中, 且这里显式启用后,
     * compiled 主链路会回退执行该分支的 {@link CaseThen} 规则.
     * </p>
     *
     * @return CaseElse
     */
    CaseElse otherwise() default @CaseElse();

    /**
     * 分支默认作用的 canonical 表达式.
     *
     * <p>
     * 当分支内部未单独覆写左侧目标时, 该表达式作为分支输出谓词的默认左侧表达式参与编译.
     * 未显式配置时, 默认回退为当前注解绑定字段对应的实体路径.
     * </p>
     *
     * @return Expr
     */
    Expr left() default @Expr(type = io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType.PATH);

    /**
     * 当前分支族的公共选项.
     *
     * <p>
     * 该选项作为每个 {@link Case} 分支的父级公共选项来源, 分支可在各自的
     * {@link Case#options()} 中继续覆盖、继承或清空.
     * </p>
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link Cases} annotations on the same element.
     *
     * @see Cases
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        /**
         * {@link Cases} 集合.
         *
         * @return {@link Cases}[]
         */
        Cases[] value();

    }

}
