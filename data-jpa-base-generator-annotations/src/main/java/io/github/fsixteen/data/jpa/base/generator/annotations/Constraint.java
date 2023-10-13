package io.github.fsixteen.data.jpa.base.generator.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 该注解类用于指定特定逻辑注解标识类的逻辑处理器执行实现类.<br>
 * 使用示例 eg:<br>
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
 *
 * @author FSixteen
 * @since 1.0.0
 */
@Documented
@Target({ ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface Constraint {

    /**
     * 特定逻辑注解标识类的逻辑处理器执行实现类.<br>
     * 
     * @return Class&lt;?&gt;
     */
    Class<?> processorBy() default Void.class;

}
