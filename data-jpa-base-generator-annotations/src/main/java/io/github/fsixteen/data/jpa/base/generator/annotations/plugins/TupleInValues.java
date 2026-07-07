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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleInValues.List;

/**
 * 多列 tuple-value 成员判断注解.
 *
 * <p>
 * 该注解用于表达类似 {@code (a, b) in ((1, 2), (3, 4))} 的语义,
 * 解释器会在 compiled 主链路中将其降级为多组 {@code and} 通过 {@code or} 拼接的稳定 JPA Predicate.
 * 当 {@link #tupleField()} 为空时, 当前注解宿主字段本身就是 tuple 数据源；否则宿主字段承担触发器语义,
 * 真实 tuple 数据源从 {@link #tupleField()} 指向的属性路径读取.
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
public @interface TupleInValues {

    /**
     * tuple 列映射集合.
     *
     * @return TupleColumn[]
     */
    TupleColumn[] columns();

    /**
     * tuple 数据源字段路径.
     *
     * @return String
     */
    String tupleField() default "";

    /**
     * 当前 tuple 谓词的公共选项.
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        TupleInValues[] value();

    }

}
