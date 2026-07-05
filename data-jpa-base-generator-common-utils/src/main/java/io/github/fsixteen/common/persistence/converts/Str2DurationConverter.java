package io.github.fsixteen.common.persistence.converts;

import java.time.Duration;
import java.util.Objects;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link String} 与实体数据类型 {@link Duration} 相互转换
 * {@link AttributeConverter} 接口.<br>
 *
 * <p>
 * 默认使用 ISO-8601 持续时间格式, 格式形如 {@code PT48H}、{@code P2DT3H4M}、{@code PT30M}.
 * </p>
 *
 * <p>
 * 空字符串将在转换为实体属性时视为 {@code null}.
 * </p>
 *
 * @author FSixteen
 * @since 1.1.0
 */
public class Str2DurationConverter implements AttributeConverter<Duration, String> {

    @Override
    public String convertToDatabaseColumn(Duration attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }
        return attribute.toString();
    }

    @Override
    public Duration convertToEntityAttribute(String dbData) {
        if (Objects.isNull(dbData) || dbData.isEmpty()) {
            return null;
        }
        return Duration.parse(dbData);
    }

}