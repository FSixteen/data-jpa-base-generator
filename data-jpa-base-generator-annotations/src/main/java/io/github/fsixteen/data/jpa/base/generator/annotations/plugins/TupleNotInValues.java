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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleNotInValues.List;

/**
 * 多列 tuple-value 非成员判断注解。
 *
 * <p>
 * 该注解与 {@link TupleInValues} 共享同一组 tuple 数据读取、归一化和类型转换规则，
 * 仅在最终 compiled 执行阶段对正向 tuple predicate 做一次整体取反。
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
public @interface TupleNotInValues {

    /**
     * tuple 列映射集合。
     *
     * @return TupleColumn[]
     */
    TupleColumn[] columns();

    /**
     * tuple 数据源字段路径。
     *
     * @return String
     */
    String tupleField() default "";

    /**
     * 当前 tuple 谓词的公共选项。
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        TupleNotInValues[] value();

    }

}
