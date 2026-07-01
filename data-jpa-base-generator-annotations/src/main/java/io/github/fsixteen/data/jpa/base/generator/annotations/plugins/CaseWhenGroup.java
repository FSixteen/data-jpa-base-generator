package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType;

/**
 * {@link CaseWhen} 的结构化命中分组。
 *
 * <p>
 * 分组中的表达式统一基于参数对象运行时求值，适合表达多叶子 AND/OR 分支命中逻辑。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface CaseWhenGroup {

    GroupComputerType.Type junction() default GroupComputerType.Type.AND;

    Compare[] compare() default {};

    Range[] range() default {};

    Membership[] membership() default {};

    NullCheck[] nullCheck() default {};

    NestedCaseWhenGroup[] groups() default {};

}
