package io.github.fsixteen.common.persistence.converts;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link String} 与实体数据类型 {@link LocalDate} 相互转换
 * {@link AttributeConverter} 接口.<br>
 * 
 * 格式化模板为 {@code yyyy-MM-dd}.
 * 
 * @see AbstractString2LocalDateTimeConverter
 * @author FSixteen
 * @since 1.0.2
 */
public class Str2LocalDateConverter extends AbstractString2LocalDateTimeConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseColumn(LocalDate attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }
        return this.localDate2Str(attribute);
    }

    @Override
    public LocalDate convertToEntityAttribute(String dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        }
        return this.str2LocalDate(dbData);
    }

}
