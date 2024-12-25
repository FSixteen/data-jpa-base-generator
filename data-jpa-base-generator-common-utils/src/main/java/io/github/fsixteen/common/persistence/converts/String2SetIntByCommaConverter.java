package io.github.fsixteen.common.persistence.converts;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link String} 与实体数据类型 {@link Set<Integer>} 通过 {@value #DECOLLATOR}
 * 分割元素相互转换 {@link AttributeConverter} 接口.<br>
 * 
 * @author FSixteen
 * @since 1.0.2
 */
public class String2SetIntByCommaConverter implements AttributeConverter<Set<Integer>, String> {
    private static final String DECOLLATOR = ",";

    @Override
    public String convertToDatabaseColumn(Set<Integer> attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        } else if (attribute.isEmpty()) {
            return "";
        } else {
            return attribute.stream().map(Objects::toString).collect(Collectors.joining(DECOLLATOR));
        }
    }

    @Override
    public Set<Integer> convertToEntityAttribute(String dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        } else if (0 == dbData.length()) {
            return Collections.emptySet();
        } else {
            return Stream.of(dbData.split(DECOLLATOR)).map((e) -> "null".equals(e) ? null : Integer.valueOf(e)).collect(Collectors.toSet());
        }
    }

}
