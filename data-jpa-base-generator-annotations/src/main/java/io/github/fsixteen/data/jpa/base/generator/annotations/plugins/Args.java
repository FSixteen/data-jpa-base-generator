package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.FunctionArgsType;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.ArgsProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.DefaultArgsProcessor;

/**
 * 自定义函数参数.<br>
 *
 * @author FSixteen
 * @since 1.0.2
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface Args {

    /**
     * 字面值.
     * 
     * @return String
     */
    String value() default "";

    /**
     * 自定义函数参数处理器.<br>
     * 当且仅当 {@link #type()} = {@link FunctionArgsType#UDFUNCTION} 时有效.<br>
     * <p>
     * 当存在 {@link #funClassName()} 时, 以 {@link #funClassName()} 计算, 放弃
     * {@link #funClass()}.<br>
     * </p>
     * <p>
     * 当不存在 {@link #funClassName()} 时, 以 {@link #funClass()} 计算.<br>
     * </p>
     * 
     * @return Class&lt;? extends ArgsProcessor&gt;
     */
    Class<? extends ArgsProcessor> funClass() default DefaultArgsProcessor.class;

    /**
     * 自定义函数参数处理器实现类.<br>
     * 当且仅当 {@link #type()} = {@link FunctionArgsType#UDFUNCTION} 时有效.<br>
     * <p>
     * 当存在 {@link #funClassName()} 时, 以 {@link #funClassName()} 计算, 放弃
     * {@link #funClass()}.<br>
     * </p>
     * <p>
     * 当不存在 {@link #funClassName()} 时, 以 {@link #funClass()} 计算.<br>
     * </p>
     *
     * @return String
     */
    String funClassName() default "";

    /**
     * 函数参数元素类型.
     * 
     * @return FunctionArgsType
     */
    FunctionArgsType type() default FunctionArgsType.COLUMN;

}
