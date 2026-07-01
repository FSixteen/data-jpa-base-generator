package io.github.fsixteen.data.jpa.base.generator.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 启用(有效)顺序.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 * @deprecated 当前 compiled 主链路已不再读取此注解。如需控制分组内谓词执行顺序，
 *             请使用
 *             {@link io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo#order()}。
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
@Documented
@Inherited
@Deprecated
public @interface Order {

    /**
     * 启用(有效)顺序.<br>
     * 
     * @return int
     */
    int value() default 0;

}
