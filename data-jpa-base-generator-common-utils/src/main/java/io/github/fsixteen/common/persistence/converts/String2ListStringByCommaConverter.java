package io.github.fsixteen.common.persistence.converts;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link String} 与实体数据类型 {@link List<String>} 通过 {@value #DECOLLATOR}
 * 分割元素相互转换 {@link AttributeConverter} 接口.<br>
 * 
 * @author FSixteen
 * @since 1.0.2
 */
public class String2ListStringByCommaConverter implements AttributeConverter<List<String>, String> {
    private static final String DECOLLATOR = ",";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        } else if (attribute.isEmpty()) {
            return "";
        } else if (1 == attribute.size()) {
            return attribute.get(0);
        } else {
            return attribute.stream().collect(Collectors.joining(DECOLLATOR));
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        } else if (0 == dbData.length()) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(Arrays.asList(dbData.split(DECOLLATOR)));
        }
    }

}
