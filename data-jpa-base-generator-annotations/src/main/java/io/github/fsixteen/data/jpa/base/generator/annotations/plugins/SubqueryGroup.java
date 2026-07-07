package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType;

/**
 * 子查询内部的谓词分组.
 *
 * <p>
 * 该注解是 canonical 子查询 where-tree 的统一输入模型, 用于直接表达子查询内部一层分组的
 * 连接关系、叶子谓词集合以及嵌套子分组. 分组中的路径表达式统一以子查询 root 为求值基准,
 * 运行时值表达式则仍从外层请求参数对象读取值.
 * </p>
 *
 * <p>
 * 支持的叶子能力包括：
 * </p>
 * <ul>
 * <li>{@link Compare}</li>
 * <li>{@link Range}</li>
 * <li>{@link Membership}</li>
 * <li>{@link NullCheck}</li>
 * </ul>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface SubqueryGroup {

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

    /**
     * 嵌套子分组.
     *
     * @return NestedSubqueryGroup[]
     */
    NestedSubqueryGroup[] groups() default {};

}
