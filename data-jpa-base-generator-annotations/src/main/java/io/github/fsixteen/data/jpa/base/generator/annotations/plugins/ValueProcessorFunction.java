package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ValueType;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.DefaultValueProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.ValueProcessor;

/**
 * 函数计算条件.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public @interface ValueProcessorFunction {

    /**
     * 参数字段指向的值类型, 默认为静态数值.<br>
     * 
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#valueType
     * @return ValueType
     */
    ValueType valueType() default ValueType.AUTO;

    /**
     * 值函数执行类.<br>
     * 
     * @return Class&lt;? extends ValueProcessor&gt;
     */
    Class<? extends ValueProcessor> processorClass() default DefaultValueProcessor.class;

}
