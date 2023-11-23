package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.FieldType;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ValueType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.LeftLike.List;

/**
 * 字符串前包含条件(select * from table_name where column_name like '%abc').<br>
 * 当且仅当参与计算值类型或函数返回值类型为{@code java.lang.String}时有效.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@Selectable
@Inherited
public @interface LeftLike {

    /**
     * 范围查询分组.<br>
     * 默认同在一组范围查询内.<br>
     *
     * @return String[]
     */
    String[] scope() default Constant.DEFAULT;

    /**
     * 条件查询分组, 默认独立组 {@code @GroupInfo("default", 0)}. <br>
     * 当 {@link #groups()} 值大于 {@code 1} 组时, 该条件可以被多条件查询分组复用.
     *
     * @return GroupInfo[]
     */
    GroupInfo[] groups() default { @GroupInfo };

    /**
     * 参与计算的最终字段. 不指定默认为当前参数字段.<br>
     *
     * @return String
     */
    String field() default "";

    /**
     * 字段(列)参与计算方式, 默认为参数字段本身参与计算.<br>
     *
     * @since 1.0.2
     * @return FieldType
     */
    FieldType fieldType() default FieldType.COLUMN;

    /**
     * 字段(列)参与计算函数.<br>
     * 当且仅当 {@link #fieldType()} = {@link FieldType#FUNCTION} 时有效.<br>
     *
     * @since 1.0.2
     * @return Function
     */
    Function fieldFunction() default @Function();

    /**
     * 字段(列)参与计算自定义函数.<br>
     * 当且仅当 {@link #fieldType()} = {@link FieldType#UDFUNCTION} 时有效.<br>
     *
     * @since 1.0.2
     * @return Function
     */
    FieldProcessorFunction fieldProcessor() default @FieldProcessorFunction();

    /**
     * 参数字段指向的值类型, 默认为静态数值.<br>
     *
     * @return ValueType
     */
    ValueType valueType() default ValueType.VALUE;

    /**
     * 值函数.<br>
     * 当且仅当 {@link #valueType()} = {@link ValueType#FUNCTION} 时有效.<br>
     * 
     * @since 1.0.2
     * @return Function
     */
    Function valueFunction() default @Function();

    /**
     * 自定义值函数.<br>
     * 当且仅当 {@link #valueType()} = {@link ValueType#UDFUNCTION} 时有效.<br>
     *
     * @return Function
     */
    ValueProcessorFunction valueProcessor() default @ValueProcessorFunction();

    /**
     * 参与计算方式.<br>
     * <br>
     * - 为<code>true</code>时, 任何时机均参与计算.<br>
     * <br>
     * - 为<code>false</code>时, 根据{@link #ignoreNull()}, {@link #ignoreEmpty()},
     * {@link #ignoreBlank()}则机参与计算.<br>
     *
     * @return boolean
     */
    boolean required() default false;

    /**
     * 逻辑反向.<br>
     *
     * @return boolean
     */
    boolean not() default false;

    /**
     * 忽略空值.<br>
     * 当元素为集合时, 判断每个元素, 忽略空值.<br>
     * 当且仅当 {@link #required()} = {@link Boolean#FALSE} 时有效.<br>
     *
     * @return boolean
     */
    boolean ignoreNull() default true;

    /**
     * 忽略空字符串值.<br>
     * 当元素为集合, 判断每个元素, 忽略空字符串值.<br>
     * 当且仅当 {@link #required()} = {@link Boolean#FALSE} 时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为 {@link java.lang.String} 或数组/集合元素类型为
     * {@link java.lang.String} 时有效.<br>
     *
     * @return boolean
     */
    boolean ignoreEmpty() default true;

    /**
     * 忽略空白字符值.<br>
     * 当元素为集合, 判断每个元素, 忽略空白字符值.<br>
     * 当且仅当 {@link #required()} = {@link Boolean#FALSE} 时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为 {@link java.lang.String} 或数组/集合元素类型为
     * {@link java.lang.String} 时有效.<br>
     *
     * @return boolean
     */
    boolean ignoreBlank() default true;

    /**
     * 删除所有前导和尾随空白字符.<br>
     * 当元素为集合, 判断每个元素, 删除所有前导和尾随空白字符.<br>
     * 当且仅当 {@link #required()} = {@link Boolean#FALSE} 时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为 {@link java.lang.String} 或数组/集合元素类型为
     * {@link java.lang.String} 时有效.<br>
     *
     * @return boolean
     */
    boolean trim() default true;

    /**
     * Defines several {@link LeftLike} annotations on the same element.
     *
     * @see LeftLike
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        /**
         * {@link LeftLike} 集合.<br>
         * 
         * @return {@link LeftLike}[]
         */
        LeftLike[] value();

    }

}
