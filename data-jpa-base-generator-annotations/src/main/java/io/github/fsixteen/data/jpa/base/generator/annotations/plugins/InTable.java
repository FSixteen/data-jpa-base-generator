package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.MultipleConditions;
import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ValueInType;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ValueType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.InTable.List;

/**
 * 夸表包含条件.<br>
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
public @interface InTable {

    /**
     * 目标实体.
     * 
     * @see javax.persistence.JoinColumn#table
     * @return Class&lt;?&gt;
     */
    Class<?> targetEntity();

    /**
     * @see javax.persistence.JoinColumn#name
     * @return String
     */
    String columnName() default "";

    /**
     * @see javax.persistence.JoinColumn#referencedColumnName
     * @return String
     */
    String referencedColumnName() default "";

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.constant.ValueInType
     * @return ValueJoinType
     */
    ValueInType valueInType() default ValueInType.TARGET;

    /**
     * 值参与计算方式.<br>
     * eg: <br>
     * 中的<br>
     * <code>
     * - io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal.class<br>
     * - io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Gt.class<br>
     * - io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Gte.class<br>
     * - io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Lt.class<br>
     * - io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Lte.class<br>
     * - io.github.fsixteen.data.jpa.base.generator.annotations.plugins.GreaterThan.class<br>
     * - io.github.fsixteen.data.jpa.base.generator.annotations.plugins.GreaterThanOrEqualTo.class<br>
     * - io.github.fsixteen.data.jpa.base.generator.annotations.plugins.LessThan.class<br>
     * - io.github.fsixteen.data.jpa.base.generator.annotations.plugins.LessThanOrEqualTo.class<br>
     * - io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Like.class<br>
     * - ......
     * </code>
     * 
     * @return Class&lt;?&gt;
     */
    Class<Annotation> valueInProcessorClass();

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#scope
     * @return String[]
     */
    String[] scope() default Constant.DEFAULT;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#groups
     * @return GroupInfo[]
     */
    GroupInfo[] groups() default { @GroupInfo };

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#field
     * @return String
     */
    String field() default "";

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#valueType
     * @return ValueType
     */
    ValueType valueType() default ValueType.VALUE;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#valueProcessor
     * @return ValueProcessorFunction
     */
    ValueProcessorFunction valueProcessor() default @ValueProcessorFunction();

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#required
     * @return boolean
     */
    boolean required() default false;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#not
     * @return boolean
     */
    boolean not() default false;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#ignoreNull
     * @return boolean
     */
    boolean ignoreNull() default true;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#ignoreEmpty
     * @return boolean
     */
    boolean ignoreEmpty() default true;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#ignoreBlank
     * @return boolean
     */
    boolean ignoreBlank() default true;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#trim
     * @return boolean
     */
    boolean trim() default true;

    /**
     * Defines several {@link InTable} annotations on the same element.
     *
     * @see InTable
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @MultipleConditions
    @interface List {
        InTable[] value();
    }
}
