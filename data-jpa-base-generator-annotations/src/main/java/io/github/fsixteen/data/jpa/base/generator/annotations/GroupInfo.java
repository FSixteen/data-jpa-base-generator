package io.github.fsixteen.data.jpa.base.generator.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;

/**
 * 分组信息.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface GroupInfo {

    /**
     * 条件分组名称.<br>
     * 
     * @return String
     */
    String value() default Constant.DEFAULT;

    /**
     * 条件顺序.<br>
     * 
     * @return int
     */
    int order() default 0;

}
