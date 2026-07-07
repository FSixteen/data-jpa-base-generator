package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType;

/**
 * {@link CaseThen} 的结构化动作分组.
 *
 * <p>
 * 分组中的路径表达式基于实体 root 解析, 值表达式继续从参数对象读取,
 * 适合一次生成多条叶子谓词并做 AND/OR 组合.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface CaseThenGroup {

    GroupComputerType.Type junction() default GroupComputerType.Type.AND;

    Compare[] compare() default {};

    Range[] range() default {};

    Membership[] membership() default {};

    NullCheck[] nullCheck() default {};

    NestedCaseThenGroup[] groups() default {};

}
