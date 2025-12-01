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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.IsNotNull.List;

/**
 * 判不为空条件(select * from table_name where column_name is not null).<br>
 * 当且仅当参与计算值类型为{@code java.lang.Boolean} 且值等于 {@link java.lang.Boolean#TRUE}
 * 时有效.<br>
 *
 * @author FSixteen
 * @since 1.0.2
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@Selectable
@Inherited
public @interface IsNotNull {

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
     * @return FieldType
     */
    FieldType fieldType() default FieldType.AUTO;

    /**
     * 字面量.<br>
     * 当且仅当 {@link #fieldType()} = {@link FieldType#LITERAL} 时有效.<br>
     * 
     * @return String
     */
    String fieldLiteral() default "";

    /**
     * 字段(列)参与计算函数.<br>
     * 当且仅当 {@link #fieldType()} = {@link FieldType#FUNCTION} 时有效.<br>
     *
     * @return Function
     */
    Function fieldFunction() default @Function();

    /**
     * 字段(列)参与计算自定义函数.<br>
     * 当且仅当 {@link #fieldType()} = {@link FieldType#UDFUNCTION} 时有效.<br>
     *
     * @return Function
     */
    FieldProcessorFunction fieldProcessor() default @FieldProcessorFunction();

    /**
     * 参与计算方式.<br>
     * <br>
     * - 为<code>true</code>时, 任何时机均参与计算.<br>
     * <br>
     * - 为<code>false</code>时, 根据{@link #ignoreNull()}则机参与计算.<br>
     *
     * @return boolean
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.IsNull
     */
    boolean required() default false;

    /**
     * 逻辑反向.<br>
     *
     * @return boolean
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.IsNull
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
         * {@link IsNotNull} 集合.<br>
         * 
         * @return {@link IsNotNull}[]
         */
        IsNotNull[] value();

    }

}
