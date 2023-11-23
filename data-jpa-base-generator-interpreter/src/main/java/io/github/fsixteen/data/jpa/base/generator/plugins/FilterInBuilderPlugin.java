package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;

/**
 * 有关<br>
 * {@link io.github.fsixteen.data.jpa.base.generator.annotations.plugins.FilterIn}<br>
 * 和<br>
 * {@link io.github.fsixteen.data.jpa.base.generator.annotations.plugins.FilterNotIn}<br>
 * 注解解释器.<br>
 * 包含, 不包含某集合元素, 并带有集合元素过滤条件的计算内容的注解解释器.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
public class FilterInBuilderPlugin<A extends Annotation> extends InBuilderPlugin<A> {

    private static Map<Class<?>, java.util.function.Predicate<Object>> PREDICATE_CACHE = new ConcurrentHashMap<>();

    public FilterInBuilderPlugin(ComparableType type) {
        super(type);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    java.util.function.Predicate<Object> getTestPredicate(AnnotationDescriptor<A> ad, Object obj) throws ReflectiveOperationException {
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
                if (java.util.function.Predicate.class.isAssignableFrom(clazz)) {
                    PREDICATE_CACHE.put(clazz, (java.util.function.Predicate<Object>) clazz.getDeclaredConstructor().newInstance());
                } else {
                    PREDICATE_CACHE.put(clazz, (e) -> false);
                }
                return PREDICATE_CACHE.get(clazz);
            }
        }
    }

}
