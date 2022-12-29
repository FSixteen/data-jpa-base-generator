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
 * @since V1.0.0
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface GroupInfo {

    @Alias(value = "name")
    String value() default Constant.DEFAULT;

    @Alias(value = "value")
    String name() default Constant.DEFAULT;

    int order() default 0;

}
