package io.github.fsixteen.data.jpa.base.generator.prepost;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface FsnPreAuthorize {

    String insert() default "";

    String update() default "";

    String delete() default "";

    String select() default "";

    String selectAll() default "";

    String selectOne() default "";

}
