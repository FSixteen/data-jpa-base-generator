package io.github.fsixteen.common.persistence.converts;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link String} 与实体数据类型 {@link Set}<{@link String}> 通过
 * {@value #DECOLLATOR}
 * 分割元素相互转换 {@link AttributeConverter} 接口.<br>
 * 
 * @author FSixteen
 * @since 1.0.2
 */
public class String2SetStringByCommaConverter implements AttributeConverter<Set<String>, String> {

    private static final String DECOLLATOR = ",";

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        } else if (attribute.isEmpty()) {
            return "";
        } else {
            return attribute.stream().map(Objects::toString).collect(Collectors.joining(DECOLLATOR));
        }
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        } else if (0 == dbData.length()) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(dbData.split(DECOLLATOR))));
        }
    }

}
