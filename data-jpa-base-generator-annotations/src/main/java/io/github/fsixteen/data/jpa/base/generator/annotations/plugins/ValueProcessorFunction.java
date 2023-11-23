package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.DefaultValueProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.ValueProcessor;

/**
 * 值函数执行信息.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface ValueProcessorFunction {

    /**
     * 值函数执行类.
     * {@linkplain io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.ValueProcessor
     * ValueProcessor} 实现类.<br>
     * <p>
     * 当存在 {@link #processorClassName()} 时, 以 {@link #processorClassName()} 计算,
     * 放弃 {@link #processorClass()}.<br>
     * </p>
     * <p>
     * 当不存在 {@link #processorClassName()} 时, 以 {@link #processorClass()} 计算,
     * 放弃 {@link #processorClassName()}.<br>
     * </p>
     * <p>
     * 当存在 {@link #processorClassName()}, 但实例化失败时, 重新以 {@link #processorClass()}
     * 计算, 放弃 {@link #processorClassName()}.<br>
     * </p>
     * 
     * @see #processorClassName()
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.ValueProcessor
     * @return Class&lt;? extends ValueProcessor&gt;
     */
    Class<? extends ValueProcessor> processorClass() default DefaultValueProcessor.class;

    /**
     * 值函数执行类名.
     * {@linkplain io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.ValueProcessor
     * ValueProcessor} 实现类.<br>
     * <p>
     * 当存在 {@link #processorClassName()} 时, 以 {@link #processorClassName()} 计算,
     * 放弃 {@link #processorClass()}.<br>
     * </p>
     * <p>
     * 当不存在 {@link #processorClassName()} 时, 以 {@link #processorClass()} 计算,
     * 放弃 {@link #processorClassName()}.<br>
     * </p>
     * <p>
     * 当存在 {@link #processorClassName()}, 但实例化失败时, 重新以 {@link #processorClass()}
     * 计算, 放弃 {@link #processorClassName()}.<br>
     * </p>
     * 
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.ValueProcessor
     * @return String
     */
    String processorClassName() default "";

}
