package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType;

/**
 * {@link CaseThenGroup} 的中层节点.
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface NestedCaseThenGroup {

    GroupComputerType.Type junction() default GroupComputerType.Type.AND;

    Compare[] compare() default {};

    Range[] range() default {};

    Membership[] membership() default {};

    NullCheck[] nullCheck() default {};

    DeepCaseThenGroup[] groups() default {};

}
