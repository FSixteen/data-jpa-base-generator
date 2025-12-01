package io.github.fsixteen.common.persistence.converts;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link Integer} 与实体数据类型 {@link LocalDate} 相互转换
 * {@link AttributeConverter} 接口.<br>
 *
 * <p>
 * 格式化模板为 {@code yyyyMMdd}.
 * </p>
 * 
 * @see AbstractNumber2LocalDateTimeConverter
 * @author FSixteen
 * @since 1.0.2
 */
public class Int2LocalDateConverter extends AbstractNumber2LocalDateTimeConverter<LocalDate, Integer> {

    @Override
    public Integer convertToDatabaseColumn(LocalDate attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }
        return this.localDate2Number(attribute);
    }

    @Override
    public LocalDate convertToEntityAttribute(Integer dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        }
        return number2LocalDate(dbData);
    }

}
