package io.github.fsixteen.data.jpa.base.generator.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 别名.<br>
 * 
 * @author FSixteen
 * @since V1.0.0
 *
 */
@Target({ METHOD })
@Retention(RUNTIME)
@Documented
public @interface Alias {
    String value();

    String desc() default "";
}
