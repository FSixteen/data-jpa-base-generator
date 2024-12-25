package io.github.fsixteen.common.persistence.converts;

import java.util.Date;
import java.util.Objects;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link Long} 与实体数据类型 {@link java.util.Date} 相互转换
 * {@link AttributeConverter} 接口.<br>
 * 
 * @author FSixteen
 * @since 1.0.2
 */
public class Timestamp2DateConverter implements AttributeConverter<Date, Long> {

    @Override
    public Long convertToDatabaseColumn(Date attribute) {
        return Objects.isNull(attribute) ? null : attribute.getTime();
    }

    @Override
    public Date convertToEntityAttribute(Long dbData) {
        return Objects.isNull(dbData) ? null : new Date(dbData);
    }

}