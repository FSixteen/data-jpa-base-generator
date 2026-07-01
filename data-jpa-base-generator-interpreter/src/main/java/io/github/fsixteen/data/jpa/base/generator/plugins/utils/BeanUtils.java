package io.github.fsixteen.data.jpa.base.generator.plugins.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Bean 反射辅助工具。
 *
 * <p>
 * 当前主要用于收集一个类型及其父类上的全部字段，
 * 供注解扫描阶段统一遍历 query model 上的候选属性。
 * </p>
 *
 * <p>
 * 该工具不负责属性可读性判断，也不处理 getter/setter 语义；
 * 它只提供基于声明字段的结构扫描能力，适合编译阶段做注解发现。
 * </p>
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
