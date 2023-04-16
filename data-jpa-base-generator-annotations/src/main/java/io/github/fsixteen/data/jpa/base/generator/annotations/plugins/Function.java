package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Function.List;

/**
 * 函数条件(select * from table_name where column_name = fun_name( args1, args2,
 * ...... )).<br>
 * 参与计算值类型或函数返回值类型为任意类型时有效.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@Selectable
public @interface Function {

    // TODO :: 扩展内容

    /**
     * Defines several {@link Function} annotations on the same element.
     *
     * @see Function
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        /**
         * {@link Function} 集体.<br>
         * 
         * @return {@link Function}[]
         */
        Function[] value();

    }

}
