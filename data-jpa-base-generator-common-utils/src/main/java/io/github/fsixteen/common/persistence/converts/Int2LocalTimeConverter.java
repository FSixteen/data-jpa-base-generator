package io.github.fsixteen.common.persistence.converts;

import java.time.LocalTime;
import java.util.Objects;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link Integer} 与实体数据类型 {@link LocalTime} 相互转换
 * {@link AttributeConverter} 接口.<br>
 *
 * <p>
 * 格式化模板为 {@code HHmmssSSS}.
 * </p>
 * 
 * @see AbstractNumber2LocalDateTimeConverter
 * @author FSixteen
 * @since 1.0.2
 */
public class Int2LocalTimeConverter extends AbstractNumber2LocalDateTimeConverter<LocalTime, Integer> {

    @Override
    public Integer convertToDatabaseColumn(LocalTime attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }
        return this.localTime2Number2(attribute);
    }

    @Override
    public LocalTime convertToEntityAttribute(Integer dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        }
        return this.number2LocalTime2(dbData);
    }

}
