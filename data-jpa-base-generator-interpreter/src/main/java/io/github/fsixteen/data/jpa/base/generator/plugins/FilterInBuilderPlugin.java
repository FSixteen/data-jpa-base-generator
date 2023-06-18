package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;

/**
 * 有关包含, 不包含某集合类型, 并带有过滤条件的计算内容的注解解释器.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
public class FilterInBuilderPlugin<A extends Annotation> extends InBuilderPlugin<A> {
    private static Map<Class<?>, java.util.function.Predicate<Object>> PREDICATE_CACHE = new ConcurrentHashMap<>();

    public FilterInBuilderPlugin(ComparableType type) {
        super(type);
    }

    @SuppressWarnings("unchecked")
    java.util.function.Predicate<Object> getTestPredicate(AnnotationDescriptor<A> ad) throws ReflectiveOperationException {
        String regexp = ad.getRegexp();
        if (null != regexp && !regexp.isEmpty()) {
            return (e) -> Pattern.matches(regexp, e.toString());
        }
        Class<?> clazz = ad.getTestClass();
        if (PREDICATE_CACHE.containsKey(clazz)) {
            return PREDICATE_CACHE.get(clazz);
        } else {
            synchronized (PREDICATE_CACHE) {
                if (PREDICATE_CACHE.containsKey(clazz)) {
                    return PREDICATE_CACHE.get(clazz);
                }
                if (!java.util.function.Predicate.class.isAssignableFrom(clazz)) {
                    PREDICATE_CACHE.put(clazz, (e) -> false);
                } else {
                    PREDICATE_CACHE.put(clazz, (java.util.function.Predicate<Object>) clazz.getDeclaredConstructor().newInstance());
                }
                return PREDICATE_CACHE.get(clazz);
            }
        }
    }

}
