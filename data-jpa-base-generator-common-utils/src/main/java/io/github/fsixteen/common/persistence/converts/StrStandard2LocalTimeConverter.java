package io.github.fsixteen.common.persistence.converts;

import java.time.LocalTime;
import java.util.Objects;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link String} 与实体数据类型 {@link LocalTime} 相互转换
 * {@link AttributeConverter} 接口.<br>
 * 
 * <p>
 * 格式化模板为 {@code HH:mm:ss}.
 * </p>
 * 
 * @see AbstractString2LocalDateTimeConverter
 * @author FSixteen
 * @since 1.0.2
 */
public class StrStandard2LocalTimeConverter extends AbstractString2LocalDateTimeConverter<LocalTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalTime attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }
        return this.localTime2Str1(attribute);
    }

    @Override
    public LocalTime convertToEntityAttribute(String dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        }
        return this.str2LocalTime1(dbData);
    }

}
