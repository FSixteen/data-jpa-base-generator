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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Cases.List;

/**
 * TODO :: 规划中.<br>
 * 分支条件(select * from table_name where (xxxxxx)).<br>
 * 用于根据 {@link #value()} 条件, 组装条件.<br>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@Selectable
public @interface Cases {

    /**
     * 执行方式执行器.
     * 
     * @return Case[]
     */
    Case[] value();

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
     * 参与计算的最终字段. 不指定默认为 {@link CaseWhen#field()} 参数字段.<br>
     *
     * @return String
     */
    String field() default "";

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
         * {@link Cases} 集合.<br>
         * 
         * @return {@link Cases}[]
         */
        Cases[] value();

    }

}
