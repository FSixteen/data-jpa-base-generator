package io.github.fsixteen.common.persistence.converts;

import java.util.Objects;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link Integer} 与实体数据类型 {@link Boolean} 相互转换
 * {@link AttributeConverter} 接口.<br>
 * 
 * @author FSixteen
 * @since 1.0.3
 */
public class NumberToBooleanConverter implements AttributeConverter<Integer, Boolean> {

    @Override
    public Boolean convertToDatabaseColumn(Integer attribute) {
        return Objects.nonNull(attribute) ? (attribute == 1) : null;
    }

    @Override
    public Integer convertToEntityAttribute(Boolean dbData) {
        return Objects.nonNull(dbData) ? (dbData ? 1 : 0) : null;
    }

}