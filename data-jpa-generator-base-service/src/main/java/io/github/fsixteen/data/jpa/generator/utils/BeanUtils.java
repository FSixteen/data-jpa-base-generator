package io.github.fsixteen.data.jpa.generator.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

/**
 * Bean 工具.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {

    /**
     * 获取指定元素对象内忽略的字段.<br>
     * 
     * @param <T> 元素类型
     * @param arg 元素
     * @return String[]
     */
    public static <T> String[] jsonIgnoreProperties(T arg) {
        if (null == arg) {
            return new String[0];
        }
        return jsonIgnoreProperties(arg.getClass());
    }

    /**
     * 获取指定元素对象内忽略的字段.<br>
     * 
     * @param <T>   元素类型
     * @param clazz 元素Class
     * @return String[]
     * @since 1.0.1
     */
    public static <T> String[] jsonIgnoreProperties(Class<T> clazz) {
        if (null == clazz) {
            return new String[0];
        }
        Set<String> ignoreSet = new HashSet<>();
        JsonIgnoreProperties ignoreProperties = clazz.getAnnotation(JsonIgnoreProperties.class);
        if (Objects.nonNull(ignoreProperties) && 0 < ignoreProperties.value().length) {
            ignoreSet.addAll(Arrays.asList(ignoreProperties.value()));
        }
        JsonIncludeProperties includeProperties = clazz.getAnnotation(JsonIncludeProperties.class);
        if (Objects.nonNull(includeProperties) && 0 < includeProperties.value().length) {
            List<String> includes = Arrays.asList(includeProperties.value());
            Set<String> fields = Stream.of(io.github.fsixteen.data.jpa.base.generator.plugins.utils.BeanUtils.getAllFields(clazz))
                .filter(it -> Modifier.isStatic(it.getModifiers())).map(Field::getName).filter(it -> !includes.contains(it)).collect(Collectors.toSet());
            ignoreSet.addAll(fields);
        }
        return ignoreSet.toArray(new String[ignoreSet.size()]);
    }

}
