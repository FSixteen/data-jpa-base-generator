package io.github.fsixteen.data.jpa.base.generator.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * compiled 主链路下的谓词角色标记。
 *
 * <p>
 * 该标记只负责声明某个注解家族是否属于 selection / existence 计算范围，不再承载
 * left/right/options 等 canonical 字段。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({ ANNOTATION_TYPE })
@Retention(RUNTIME)
@Documented
@Inherited
public @interface PredicateRole {

    /**
     * 新命名的查询角色声明。
     *
     * @return boolean
     */
    boolean selection() default false;

    /**
     * 新命名的存在性角色声明。
     *
     * @return boolean
     */
    boolean existence() default false;

    /**
     * `selection` 的别名字段。
     *
     * @return boolean
     */
    boolean selectable() default false;

    /**
     * `existence` 的别名字段。
     *
     * @return boolean
     */
    boolean existed() default false;

}
