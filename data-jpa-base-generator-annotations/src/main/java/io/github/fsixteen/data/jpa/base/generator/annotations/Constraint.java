package io.github.fsixteen.data.jpa.base.generator.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 逻辑处理器.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
@Documented
@Target({ ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface Constraint {

    /**
     * 逻辑处理器执行类<br>
     * .
     * 
     * @return Class&lt;?&gt;
     */
    Class<?> processorBy() default Void.class;

}
