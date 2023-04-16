package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.Existed;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ValueType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Unique.List;

/**
 * 唯一性判断等值条件(select * from table_name where column_name = '123').<br>
 * 当且仅当参与计算值类型或函数返回值类型为{@code java.lang.Comparable}时有效.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@Existed
public @interface Unique {

    /**
     * 范围查询分组.<br>
     * 默认同在一组范围查询内.<br>
     * 
     * @return String[]
     */
    String[] scope() default Constant.DEFAULT;

    /**
     * 条件查询分组, 默认独立组<code>@GroupInfo("default", 0)</code>. <br>
     * 当<code>groups</code>值大于<code>1</code>组时, 该条件可以被多条件查询分组复用.
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
     * 参数字段指向的值类型, 默认为静态数值.<br>
     *
     * @return ValueType
     */
    ValueType valueType() default ValueType.VALUE;

    /**
     * 值函数.<br>
     * 当且仅当<code>valueType = ValueType.FUNCTION</code>时有效.<br>
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
     * {@link
     * #ignoreBlank()}则机参与计算.<br>
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
     * 当元素为集合, 判断每个元素, 忽略空值.<br>
     * 当且仅当<code>required = false</code>时有效.<br>
     *
     * @return boolean
     */
    boolean ignoreNull() default true;

    /**
     * 忽略空字符串值.<br>
     * 当元素为集合, 判断每个元素, 忽略空字符串值.<br>
     * 当且仅当<code>required = false</code>时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为{@code java.lang.String}时有效.<br>
     *
     * @return boolean
     */
    boolean ignoreEmpty() default true;

    /**
     * 忽略空白字符值.<br>
     * 当元素为集合, 判断每个元素, 忽略空白字符值.<br>
     * 当且仅当<code>required = false</code>时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为{@code java.lang.String}时有效.<br>
     *
     * @return boolean
     */
    boolean ignoreBlank() default true;

    /**
     * 删除所有前导和尾随空白字符.<br>
     * 当元素为集合, 判断每个元素, 删除所有前导和尾随空白字符.<br>
     * 当且仅当<code>required = false</code>时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为{@code java.lang.String}时有效.<br>
     *
     * @return boolean
     */
    boolean trim() default true;

    /**
     * Defines several {@link Unique} annotations on the same element.
     *
     * @see Unique
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        /**
         * {@link Unique} 集体.<br>
         * 
         * @return {@link Unique}[]
         */
        Unique[] value();

    }

}
