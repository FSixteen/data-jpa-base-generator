package io.github.fsixteen.data.jpa.base.generator.plugins.codecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.TargetType;

/**
 * {@link TargetType} 运行时转换器。
 *
 * <p>
 * 该类型将旧的 {@link TargetType} 输入语义下沉为统一转换入口，
 * 主要服务于 {@code CollectionPolicy}、split/range 值解析和其它仍通过
 * {@link TargetType} 声明目标类型的场景。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class TargetTypeConverters {

    private TargetTypeConverters() {
    }

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

    public static List<Object> parseCollection(final String raw, final String decollator, final TargetType targetType, final String format) {
        List<String> tokens = LiteralCodecs.split(raw, decollator);
        List<Object> values = new ArrayList<Object>(tokens.size());
        for (String token : tokens) {
            values.add(parse(token, targetType, format));
        }
        return values;
    }

}
