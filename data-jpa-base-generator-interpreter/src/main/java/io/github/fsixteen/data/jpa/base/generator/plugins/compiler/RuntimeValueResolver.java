package io.github.fsixteen.data.jpa.base.generator.plugins.compiler;

import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FieldValueExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.support.ReadablePropertySupport;

/**
 * 运行时参数值解析器.
 *
 * <p>
 * canonical 表达式在 {@code VALUE / VALUE_PATH} 模式下, 既可能读取当前注解绑定字段,
 * 也可能读取请求参数对象上的其他属性路径. 该类型统一承接这两类读取逻辑.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class RuntimeValueResolver {

    private RuntimeValueResolver() {
    }

    /**
     * 解析字段值表达式在当前运行时上下文中的真实值.
     *
     * <p>
     * 当表达式未显式指定 `valueField` 时, 直接复用当前字段值；
     * 否则从参数对象上读取对应路径.
     * </p>
     *
     * @param expression        字段值表达式
     * @param args              当前请求或查询对象
     * @param currentFieldValue 当前字段值
     * @return 解析后的运行时值
     */
    public static Object resolve(final FieldValueExpression expression, final Object args, final Object currentFieldValue) {
        if (Objects.isNull(expression) || isBlank(expression.getValueField())) {
            return currentFieldValue;
        }
        return readPath(args, expression.getValueField());
    }

    /**
     * 按点路径从任意对象上逐段读取属性值.
     *
     * <p>
     * 读取过程中一旦某一段结果为 {@code null}, 立即返回 {@code null},
     * 从而与常见 Bean 路径访问的空值短路语义保持一致.
     * </p>
     *
     * @param bean 起始对象
     * @param path 点路径
     * @return 路径最终读取到的值；当起始对象为空或路径为空白时返回起始对象本身
     */
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

    /**
     * 读取对象上的单个属性段.
     *
     * @param bean      当前对象
     * @param fieldName 属性名
     * @return 该属性段的值
     */
    private static Object readSegment(final Object bean, final String fieldName) {
        return ReadablePropertySupport.read(bean, fieldName);
    }

    /**
     * 判断字符串是否为空白.
     *
     * @param value 待判断字符串
     * @return 为 {@code null} 或去除首尾空白后为空串时返回 {@code true}
     */
    private static boolean isBlank(final String value) {
        return Objects.isNull(value) || value.trim().isEmpty();
    }

}
