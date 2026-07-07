package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.reflect.Method;

import io.github.fsixteen.data.jpa.base.generator.plugins.utils.BeanUtils;

final class CompiledPropertyBindings {

    private CompiledPropertyBindings() {
    }

    static String propertyName(final Method method) {
        String propertyName = BeanUtils.readablePropertyName(method);
        if (null == propertyName) {
            throw new IllegalArgumentException("Predicate annotation method binding requires a Java Bean getter: " + method);
        }
        return propertyName;
    }

}
