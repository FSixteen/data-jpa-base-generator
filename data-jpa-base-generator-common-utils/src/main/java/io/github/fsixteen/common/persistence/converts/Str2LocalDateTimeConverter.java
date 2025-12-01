package io.github.fsixteen.common.persistence.converts;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link String} 与实体数据类型 {@link LocalDateTime} 相互转换
 * {@link AttributeConverter} 接口.<br>
 * 
 * <p>
 * 格式化模板为 {@code yyyy-MM-dd HH:mm:ss.SSS}.
 * </p>
 * 
 * @see AbstractString2LocalDateTimeConverter
 * @author FSixteen
 * @since 1.0.2
 */
public class Str2LocalDateTimeConverter extends AbstractString2LocalDateTimeConverter<LocalDateTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }
        return this.localDate2Str(attribute.toLocalDate()) + " " + this.localTime2Str2(attribute.toLocalTime());
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        }
        String[] s = dbData.split(" ");
        return LocalDateTime.of(this.str2LocalDate(s[0]), this.str2LocalTime2(s[1]));
    }

}
