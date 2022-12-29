package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.MultipleConditions;
import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ValueType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.GreaterThanOrEqualTo.List;

/**
 * 大于等于条件(select * from table_name where column_name &gt;= '123').<br>
 * 当且仅当参与计算值类型或函数返回值类型为{@code java.lang.Comparable}时有效.<br>
 * 
 * @author FSixteen
 * @since V1.0.0
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@Selectable
public @interface GreaterThanOrEqualTo {

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#scope
     */
    String[] scope() default Constant.DEFAULT;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#groups
     */
    GroupInfo[] groups() default { @GroupInfo };

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#field
     */
    String field() default "";

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#valueType
     */
    ValueType valueType() default ValueType.VALUE;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#valueProcessor
     */
    ValueProcessorFunction valueProcessor() default @ValueProcessorFunction();

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#required
     */
    boolean required() default false;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#not
     */
    boolean not() default false;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#ignoreNull
     */
    boolean ignoreNull() default true;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#ignoreEmpty
     */
    boolean ignoreEmpty() default true;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#ignoreBlank
     */
    boolean ignoreBlank() default true;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#trim
     */
    boolean trim() default true;

    /**
     * Defines several {@link GreaterThanOrEqualTo} annotations on the same element.
     *
     * @see GreaterThanOrEqualTo
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @MultipleConditions
    @interface List {
        GreaterThanOrEqualTo[] value();
    }
}
