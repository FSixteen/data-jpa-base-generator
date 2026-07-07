package io.github.fsixteen.data.jpa.base.generator.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.List;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;

/**
 * 分组聚合规则声明. <br>
 *
 * <p>
 * 当前 compiled 主链路会先按 `@PredicateOptions.groups` 收集叶子谓词,
 * 再用该注解声明“组如何继续向上合并”.
 * 它是当前 grouped predicate model 的类型级组装入口：
 * 负责定义某个组在指定 scope 下如何聚合、以及继续路由到哪些父组.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.0
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@Inherited
public @interface GroupComputerType {

    /**
     * 规则生效的 scope. <br>
     *
     * @return String[]
     */
    String[] scope() default Constant.DEFAULT;

    /**
     * 下一层父组路由配置.
     *
     * @return GroupInfo[]
     */
    GroupInfo[] groups() default { @GroupInfo };

    /**
     * 当前规则声明的组名.
     *
     * <p>
     * 当取值为
     * {@code io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant.GLOBAL}
     * 时, 表示直接作用于最终谓词合并阶段.
     * </p>
     *
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo#value()
     * @return String
     */
    String value() default Constant.DEFAULT;

    /**
     * 当前组内叶子或子组的聚合方式. <br>
     * 
     * @return Type
     */
    Type type() default Type.AND;

    /**
     * 聚合方式枚举. <br>
     * 
     * @author FSixteen
     * @since 1.0.0
     */
    public static enum Type {
        /**
         * 并集.
         */
        AND,

        /**
         * 或集.
         */
        OR;
    }

    /**
     * Defines several {@link GroupComputerType} annotations on the same
     * element.
     *
     * @see GroupComputerType
     */
    @Target({ TYPE })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        /**
         * {@link GroupComputerType} 集合.<br>
         * 
         * @return {@link GroupComputerType}[]
         */
        GroupComputerType[] value();

    }

}
