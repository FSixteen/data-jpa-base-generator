package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;

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
@Documented
@Selectable
public @interface Case {

    /**
     * 执行方式执行器.
     * 
     * @return CaseWhen
     */
    CaseWhen when();

    /**
     * 执行方式执行器.
     * 
     * @return CaseWhen
     */
    CaseThen then();

    /**
     * 参与计算的最终字段. 不指定默认为 {@link Cases#field()} 参数字段.<br>
     *
     * @return String
     */
    String field() default "";

}
