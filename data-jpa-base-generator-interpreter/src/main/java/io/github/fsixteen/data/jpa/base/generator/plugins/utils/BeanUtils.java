package io.github.fsixteen.data.jpa.base.generator.plugins.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Bean utils.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public class BeanUtils {

    /**
     * Returns an array of Field objects reflecting all the fields declared by
     * the class or interface represented by this Class object. This includes
     * public, protected, default (package) access, private fields, and
     * inherited fields.<br>
     *
     * @param clazz The {@code Class} object
     * @return an array of Field objects
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
