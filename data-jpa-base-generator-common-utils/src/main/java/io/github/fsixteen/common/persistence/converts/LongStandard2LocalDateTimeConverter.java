package io.github.fsixteen.common.persistence.converts;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link Long} 与实体数据类型 {@link LocalDateTime} 相互转换
 * {@link AttributeConverter} 接口.<br>
 *
 * <p>
 * 格式化模板为 {@code yyyyMMddHHmmss}.
 * </p>
 * 
 * @see AbstractNumber2LocalDateTimeConverter
 * @author FSixteen
 * @since 1.0.2
 */
public class LongStandard2LocalDateTimeConverter extends AbstractNumber2LocalDateTimeConverter<LocalDateTime, Long> {

    @Override
    public Long convertToDatabaseColumn(LocalDateTime attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }
        return this.localDate2Number(attribute.toLocalDate()) * 1_000_000L + this.localTime2Number1(attribute.toLocalTime());
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Long dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        }
        return LocalDateTime.of(this.number2LocalDate((int) (dbData / 1_000_000)), this.number2LocalTime1((int) (dbData % 1_000_000)));
    }

}
