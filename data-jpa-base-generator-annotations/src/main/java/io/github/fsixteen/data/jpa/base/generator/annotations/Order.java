package io.github.fsixteen.data.jpa.base.generator.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 启用(有效)顺序.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Documented
@Inherited
public @interface Order {

    /**
     * 启用(有效)顺序.<br>
     * 
     * @return int
     */
    int value() default 0;

}
