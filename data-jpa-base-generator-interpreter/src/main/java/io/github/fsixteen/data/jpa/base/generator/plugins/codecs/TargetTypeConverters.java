package io.github.fsixteen.data.jpa.base.generator.plugins.codecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.TargetType;

/**
 * {@link TargetType} 运行时转换器.
 *
 * <p>
 * 该类型将旧的 {@link TargetType} 输入语义下沉为统一转换入口,
 * 主要服务于 {@code CollectionPolicy}、split/range 值解析和其它仍通过
 * {@link TargetType} 声明目标类型的场景.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class TargetTypeConverters {

    private TargetTypeConverters() {
    }

    /**
     * 按旧版 {@link TargetType} 语义解析单个字符串值.
     *
     * <p>
     * 该方法是旧版目标类型声明到统一 {@link LiteralCodecs} 解析链路的桥接层.
     * 当目标类型为空时, 按 {@link TargetType#DEFAULT} 处理.
     * </p>
     *
     * @param raw        原始字符串值
     * @param targetType 目标类型声明
     * @param format     日期时间格式
     * @return 解析后的目标值
     */
    public static Object parse(final String raw, final TargetType targetType, final String format) {
        TargetType actualType = Objects.isNull(targetType) ? TargetType.DEFAULT : targetType;
        switch (actualType) {
            case DEFAULT:
                return raw;
            case TO_STR:
                return LiteralCodecs.parse(raw, String.class);
            case TO_BOOLEAN:
                return LiteralCodecs.parse(raw, Boolean.class);
            case TO_BIGDECIMAL:
                return LiteralCodecs.parse(raw, java.math.BigDecimal.class);
            case TO_DOUBLE:
                return LiteralCodecs.parse(raw, Double.class);
            case TO_FLOAT:
                return LiteralCodecs.parse(raw, Float.class);
            case TO_SHORT:
                return LiteralCodecs.parse(raw, Short.class);
            case TO_INTEGER:
                return LiteralCodecs.parse(raw, Integer.class);
            case TO_LONG:
            case TO_TS:
                return LiteralCodecs.parse(raw, Long.class);
            case TO_TS_TO_DATE:
                return LiteralCodecs.parse(raw, java.util.Date.class);
            case TO_TS_TO_LD:
                return LiteralCodecs.parseEpoch(raw, java.time.LocalDate.class);
            case TO_TS_TO_LT:
                return LiteralCodecs.parseEpoch(raw, java.time.LocalTime.class);
            case TO_TS_TO_LDT:
                return LiteralCodecs.parseEpoch(raw, java.time.LocalDateTime.class);
            case TO_DATE:
                return LiteralCodecs.parse(raw, java.util.Date.class, format);
            case TO_LD:
                return LiteralCodecs.parse(raw, java.time.LocalDate.class, format);
            case TO_LT:
                return LiteralCodecs.parse(raw, java.time.LocalTime.class, format);
            case TO_LDT:
                return LiteralCodecs.parse(raw, java.time.LocalDateTime.class, format);
            default:
                return actualType.parse(raw);
        }
    }

    /**
     * 按旧版 {@link TargetType} 语义拆分并解析集合值.
     *
     * @param raw        原始集合字符串
     * @param decollator 分隔符
     * @param targetType 目标类型声明
     * @param format     日期时间格式
     * @return 解析后的集合值列表
     */
    public static List<Object> parseCollection(final String raw, final String decollator, final TargetType targetType, final String format) {
        List<String> tokens = LiteralCodecs.split(raw, decollator);
        List<Object> values = new ArrayList<Object>(tokens.size());
        for (String token : tokens) {
            values.add(parse(token, targetType, format));
        }
        return values;
    }

}
