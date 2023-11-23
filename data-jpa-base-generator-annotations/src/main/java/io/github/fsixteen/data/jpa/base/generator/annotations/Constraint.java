package io.github.fsixteen.data.jpa.base.generator.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 该注解类用于指定特定逻辑注解标识类的逻辑处理器执行实现类.<br>
 * 使用示例 eg:<br>
 * <p>
 * 方式一:<br>
 * 
 * <pre>
 * &#64;Target({ FIELD, METHOD })
 * &#64;Retention(RUNTIME)
 * &#64;Repeatable(List.class)
 * &#64;Documented
 * &#64;Selectable
 * &#64;Constraint(processorBy = io.github.fsixteen.data.jpa.base.generator.plugins.ComparableBuilderPlugin.class)
 * public &#64;interface Equal {
 *     .....
 * }
 * </pre>
 * <p>
 * 方式二:<br>
 * 
 * <pre>
 * &#64;Target({ FIELD, METHOD })
 * &#64;Retention(RUNTIME)
 * &#64;Repeatable(List.class)
 * &#64;Documented
 * &#64;Selectable
 * &#64;Constraint(processorByClassName = "io.github.fsixteen.data.jpa.base.generator.plugins.ComparableBuilderPlugin")
 * public &#64;interface Equal {
 *     .....
 * }
 * </pre>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
@Documented
@Target({ ElementType.TYPE })
@Retention(RUNTIME)
@Inherited
public @interface Constraint {

    /**
     * 特定逻辑注解标识类的逻辑处理器执行实现类.<br>
     * 
     * @return Class&lt;?&gt;
     */
    Class<?> processorBy() default Void.class;

    /**
     * 特定逻辑注解标识类的逻辑处理器执行实现类完整名称.<br>
     * 
     * @return String
     * @since 1.0.2
     */
    String processorByClassName() default "";

}
