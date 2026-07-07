package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.plugins.codecs.LiteralCodecs;

/**
 * tuple 列值运行时转换器.
 *
 * <p>
 * 当前组件负责把 tuple 原始值转换为最终可安全参与 JPA 比较的 Java 类型,
 * 优先使用列上显式配置的目标类型, 其次回退到主查询路径解析出的 {@code javaType}.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
final class TupleValueConverter {

    private TupleValueConverter() {
    }

    /**
     * 将 tuple 原始列值转换为目标 Java 类型.
     *
     * <p>
     * 转换顺序遵循“先快速接受兼容值, 再按字符串、数值、枚举、布尔、字符等特例处理, 最后失败即报错”的策略,
     * 以尽量贴近查询比较阶段真实需要的类型形态.
     * </p>
     *
     * @param rawValue   原始列值
     * @param targetType 目标类型
     * @param format     字面量解析格式, 供日期时间等类型转换使用
     * @return 转换后的值；当原值为空、目标类型为空或目标类型为 {@link Object} 时直接返回原值
     * @throws IllegalArgumentException 当当前原值无法转换为目标类型时抛出
     */
    static Object convert(final Object rawValue, final Class<?> targetType, final String format) {
        if (Objects.isNull(rawValue) || Objects.isNull(targetType) || Object.class == targetType) {
            return rawValue;
        }
        Class<?> actualTargetType = wrapPrimitive(targetType);
        if (actualTargetType.isInstance(rawValue)) {
            return rawValue;
        }
        if (rawValue instanceof String) {
            return LiteralCodecs.parse(String.class.cast(rawValue), actualTargetType, format);
        }
        if (rawValue instanceof Number && Number.class.isAssignableFrom(actualTargetType)) {
            return convertNumber((Number) rawValue, actualTargetType);
        }
        if (actualTargetType.isEnum()) {
            return LiteralCodecs.parse(Objects.toString(rawValue), actualTargetType, format);
        }
        if (String.class == actualTargetType) {
            return Objects.toString(rawValue, null);
        }
        if (Boolean.class == actualTargetType && rawValue instanceof Number) {
            return Boolean.valueOf(0 != ((Number) rawValue).intValue());
        }
        if (Character.class == actualTargetType) {
            String text = Objects.toString(rawValue, "");
            if (text.isEmpty()) {
                return null;
            }
            return Character.valueOf(text.charAt(0));
        }
        if (rawValue instanceof CharSequence) {
            return LiteralCodecs.parse(rawValue.toString(), actualTargetType, format);
        }
        if (actualTargetType.isAssignableFrom(rawValue.getClass())) {
            return rawValue;
        }
        throw new IllegalArgumentException("Unsupported tuple value conversion from " + rawValue.getClass().getName() + " to " + actualTargetType.getName());
    }

    /**
     * 将 primitive 类型包装为对应的包装类型.
     *
     * @param type 原始类型
     * @return 若输入为 primitive, 则返回其包装类型；否则返回原类型
     */
    private static Class<?> wrapPrimitive(final Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        if (int.class == type) {
            return Integer.class;
        }
        if (long.class == type) {
            return Long.class;
        }
        if (short.class == type) {
            return Short.class;
        }
        if (byte.class == type) {
            return Byte.class;
        }
        if (double.class == type) {
            return Double.class;
        }
        if (float.class == type) {
            return Float.class;
        }
        if (boolean.class == type) {
            return Boolean.class;
        }
        if (char.class == type) {
            return Character.class;
        }
        return type;
    }

    /**
     * 在目标类型同属数值体系时执行受控数值转换.
     *
     * @param number     原始数值
     * @param targetType 目标数值类型
     * @return 转换后的数值对象；若未命中特殊分支则返回原始数值对象
     */
    private static Object convertNumber(final Number number, final Class<?> targetType) {
        if (Integer.class == targetType) {
            return Integer.valueOf(number.intValue());
        }
        if (Long.class == targetType) {
            return Long.valueOf(number.longValue());
        }
        if (Short.class == targetType) {
            return Short.valueOf(number.shortValue());
        }
        if (Byte.class == targetType) {
            return Byte.valueOf(number.byteValue());
        }
        if (Double.class == targetType) {
            return Double.valueOf(number.doubleValue());
        }
        if (Float.class == targetType) {
            return Float.valueOf(number.floatValue());
        }
        if (BigDecimal.class == targetType) {
            return new BigDecimal(number.toString());
        }
        if (BigInteger.class == targetType) {
            return BigInteger.valueOf(number.longValue());
        }
        return number;
    }

}
