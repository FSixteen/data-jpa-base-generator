package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType;

/**
 * 子查询内部的深层谓词分组.
 *
 * <p>
 * 该类型用于承接 {@link NestedSubqueryGroup} 的下一层分组, 避免 Java 注解元素形成循环定义.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface DeepSubqueryGroup {

    /**
     * 分组连接类型, 默认为 AND.
     *
     * @return GroupComputerType.Type
     */
    GroupComputerType.Type junction() default GroupComputerType.Type.AND;

    /**
     * 比较条件列表.
     *
     * @return Compare[]
     */
    Compare[] compare() default {};

    /**
     * 范围条件列表.
     *
     * @return Range[]
     */
    Range[] range() default {};

    /**
     * 成员判断条件列表.
     *
     * @return Membership[]
     */
    Membership[] membership() default {};

    /**
     * 空值判断条件列表.
     *
     * @return NullCheck[]
     */
    NullCheck[] nullCheck() default {};

}
