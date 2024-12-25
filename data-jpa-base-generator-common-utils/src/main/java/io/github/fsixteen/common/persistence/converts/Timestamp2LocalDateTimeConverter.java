package io.github.fsixteen.common.persistence.converts;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link Long} 与实体数据类型 {@link LocalDateTime} 相互转换
 * {@link AttributeConverter} 接口.<br>
 * 
 * @author FSixteen
 * @since 1.0.2
 */
public class Timestamp2LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Long> {

    @Override
    public Long convertToDatabaseColumn(LocalDateTime attribute) {
        return Objects.isNull(attribute) ? null : attribute.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Long dbData) {
        return Objects.isNull(dbData) ? null : LocalDateTime.ofInstant(Instant.ofEpochMilli(dbData), ZoneId.systemDefault());
    }

}