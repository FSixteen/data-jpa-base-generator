package io.github.fsixteen.common.persistence.converts;

import java.util.Objects;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link Boolean} 与实体数据类型 {@link Integer} 相互转换
 * {@link AttributeConverter} 接口.<br>
 * 
 * @author FSixteen
 * @since 1.0.3
 */
public class BooleanToNumberConverter implements AttributeConverter<Boolean, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Boolean attribute) {
        return Objects.nonNull(attribute) ? (attribute ? 1 : 0) : null;
    }

    @Override
    public Boolean convertToEntityAttribute(Integer dbData) {
        return Objects.nonNull(dbData) ? (dbData == 1) : null;
    }

}