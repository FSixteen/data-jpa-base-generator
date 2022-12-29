package io.github.fsixteen.data.jpa.base.generator.plugins.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author FSixteen
 * @since V1.0.0
 */
public class BeanUtils {

    /**
     * 获取类的所有字段内容, 含超类.<br>
     *
     * @param clazz
     * @return Field[]
     */
    public static Field[] getAllFields(Class<?> clazz) {
        Set<Field> fieldList = new HashSet<>();
        while (Objects.nonNull(clazz)) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fieldList.toArray(new Field[fieldList.size()]);
    }

}
