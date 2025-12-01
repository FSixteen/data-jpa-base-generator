package io.github.fsixteen.common.persistence.converts;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link Integer} 通过二进制位展开成实体数据类型 {@link Set}<{@link Integer}> 元素相互转换
 * {@link AttributeConverter} 接口.<br>
 * 
 * @author FSixteen
 * @since 1.0.2
 */
public class Int2SetIntByBitConverter implements AttributeConverter<Set<Integer>, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Set<Integer> attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        } else if (attribute.isEmpty()) {
            return 0;
        } else {
            return attribute.stream().reduce(0, (l, r) -> l | r, (l, r) -> l | r);
        }
    }

    @Override
    public Set<Integer> convertToEntityAttribute(Integer dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        } else if (0L == dbData) {
            return Collections.emptySet();
        } else {
            Set<Integer> eles = new HashSet<>();
            int temp = dbData;
            int offset = 0;
            while (0 < temp) {
                if ((temp & 1) == 1) {
                    eles.add(1 << offset);
                }
                temp >>= 1;
                offset++;
            }
            return eles;
        }
    }

}
