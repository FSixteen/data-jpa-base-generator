package io.github.fsixteen.common.persistence.converts;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link String} 与实体数据类型 {@link Set}<{@link String}> 通过
 * {@value #DELIMITER_CHAR} 分割元素相互转换 {@link AttributeConverter} 接口.<br>
 * 
 * @author FSixteen
 * @since 1.0.2
 */
public class String2SetStringByCommaConverter extends AbstractString2CollectionByCommaConverter<Set<String>> {

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        return this.collection2String(attribute);
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        Set<String> eles = this.string2Collection(dbData, new HashSet<String>());
        if (Objects.isNull(eles)) {
            return null;
        } else if (eles.isEmpty()) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(eles);
        }
    }

}
