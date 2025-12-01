package io.github.fsixteen.common.persistence.converts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link Long} 通过二进制位展开成实体数据类型 {@link List}<{@link Long}> 元素相互转换
 * {@link AttributeConverter} 接口.<br>
 * 
 * @author FSixteen
 * @since 1.0.2
 */
public class Long2ListLongByBitConverter implements AttributeConverter<List<Long>, Long> {

    @Override
    public Long convertToDatabaseColumn(List<Long> attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        } else if (attribute.isEmpty()) {
            return 0L;
        } else {
            return attribute.stream().reduce(0L, (l, r) -> l | r, (l, r) -> l | r);
        }
    }

    @Override
    public List<Long> convertToEntityAttribute(Long dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        } else if (0L == dbData) {
            return Collections.emptyList();
        } else {
            List<Long> eles = new ArrayList<>();
            long temp = dbData;
            long offset = 0L;
            while (0 < temp) {
                if ((temp & 1) == 1) {
                    eles.add(1L << offset);
                }
                temp >>= 1;
                offset++;
            }
            return eles;
        }
    }

}
