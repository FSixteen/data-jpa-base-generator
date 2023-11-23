package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.DefaultFieldProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.FieldProcessor;

/**
 * 字段(列)处理器执行信息.<br>
 *
 * @author FSixteen
 * @since 1.0.2
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface FieldProcessorFunction {

    /**
     * 字段(列)处理器执行类.
     * {@linkplain io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.FieldProcessor
     * ValueProcessor} 实现类.
     * <p>
     * 当存在 {@link #processorClassName()} 时, 以 {@link #processorClassName()} 计算,
     * 放弃 {@link #processorClass()}.
     * </p>
     * <p>
     * 当不存在 {@link #processorClassName()} 时, 以 {@link #processorClass()} 计算,
     * 放弃 {@link #processorClassName()}.
     * </p>
     * <p>
     * 当存在 {@link #processorClassName()}, 但实例化失败时, 重新以 {@link #processorClass()}
     * 计算, 放弃 {@link #processorClassName()}.
     * </p>
     * 
     * @see #processorClassName()
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.FieldProcessor
     * @return Class&lt;? extends ValueProcessor&gt;
     */
    Class<? extends FieldProcessor> processorClass() default DefaultFieldProcessor.class;

    /**
     * 字段(列)处理器执行类名.
     * {@linkplain io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.FieldProcessor
     * ValueProcessor} 实现类.
     * <p>
     * 当存在 {@link #processorClassName()} 时, 以 {@link #processorClassName()} 计算,
     * 放弃 {@link #processorClass()}.
     * </p>
     * <p>
     * 当不存在 {@link #processorClassName()} 时, 以 {@link #processorClass()} 计算,
     * 放弃 {@link #processorClassName()}.
     * </p>
     * <p>
     * 当存在 {@link #processorClassName()}, 但实例化失败时, 重新以 {@link #processorClass()}
     * 计算, 放弃 {@link #processorClassName()}.
     * </p>
     * 
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.FieldProcessor
     * @return String
     */
    String processorClassName() default "";

}
