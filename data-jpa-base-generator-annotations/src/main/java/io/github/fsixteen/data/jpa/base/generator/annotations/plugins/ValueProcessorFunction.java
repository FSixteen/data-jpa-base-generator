package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ValueType;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.DefaultValueProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.ValueProcessor;

/**
 * 函数计算条件.<br>
 * 
 * @author FSixteen
 * @since V1.0.0
 */
public @interface ValueProcessorFunction {

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#valueProcessorType
     */
    ValueType valueType() default ValueType.AUTO;

    Class<? extends ValueProcessor> processorClass() default DefaultValueProcessor.class;

}
