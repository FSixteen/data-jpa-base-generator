package io.github.fsixteen.common.persistence.converts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link String} 与实体数据类型 {@link List}<{@link String}> 通过
 * {@value #DELIMITER_CHAR} 分割元素相互转换 {@link AttributeConverter} 接口.<br>
 * 
 * @author FSixteen
 * @since 1.0.2
 */
public class String2ListStringByCommaConverter extends AbstractString2CollectionByCommaConverter<List<String>> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return this.collection2String(attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        List<String> eles = this.string2Collection(dbData, new ArrayList<String>());
        if (Objects.isNull(eles)) {
            return null;
        } else if (eles.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(eles);
        }
    }

}
