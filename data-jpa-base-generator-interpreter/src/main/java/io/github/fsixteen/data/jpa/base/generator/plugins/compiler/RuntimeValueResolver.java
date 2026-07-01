package io.github.fsixteen.data.jpa.base.generator.plugins.compiler;

import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FieldValueExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.support.ReadablePropertySupport;

/**
 * 运行时参数值解析器。
 *
 * <p>
 * canonical 表达式在 {@code VALUE / VALUE_PATH} 模式下，既可能读取当前注解绑定字段，
 * 也可能读取请求参数对象上的其他属性路径。该类型统一承接这两类读取逻辑。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class RuntimeValueResolver {

    private RuntimeValueResolver() {
    }

    public static Object resolve(final FieldValueExpression expression, final Object args, final Object currentFieldValue) {
        if (Objects.isNull(expression) || isBlank(expression.getValueField())) {
            return currentFieldValue;
        }
        return readPath(args, expression.getValueField());
    }

    public static Object readPath(final Object bean, final String path) {
        if (Objects.isNull(bean) || isBlank(path)) {
            return bean;
        }
        Object current = bean;
        for (String segment : path.split("\\.")) {
            if (Objects.isNull(current)) {
                return null;
            }
            current = readSegment(current, segment);
        }
        return current;
    }

    private static Object readSegment(final Object bean, final String fieldName) {
        return ReadablePropertySupport.read(bean, fieldName);
    }

    private static boolean isBlank(final String value) {
        return Objects.isNull(value) || value.trim().isEmpty();
    }

}
