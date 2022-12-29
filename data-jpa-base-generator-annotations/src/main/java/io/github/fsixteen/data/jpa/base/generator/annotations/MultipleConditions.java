package io.github.fsixteen.data.jpa.base.generator.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 用于标记同一元素多重同类型条件等.<br>
 * 
 * @author FSixteen
 * @since V1.0.0
 */
@Target({ ANNOTATION_TYPE })
@Retention(RUNTIME)
@Documented
public @interface MultipleConditions {
}
