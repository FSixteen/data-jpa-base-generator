package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
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

    private static Map<String, java.util.function.Predicate<Object>> STRING_PREDICATE_CACHE = new ConcurrentHashMap<>();

    private static Map<Class<?>, java.util.function.Predicate<Object>> CLASS_PREDICATE_CACHE = new ConcurrentHashMap<>();

    public FilterInBuilderPlugin(ComparableType type) {
        super(type);
    }

    /**
     * {@inheritDoc}.
     * 
     * 所有异常均抛出.
     */
    @Override
    @SuppressWarnings("unchecked")
    java.util.function.Predicate<Object> getTestPredicate(AnnotationDescriptor<A> ad, Object obj) throws ReflectiveOperationException {
        // 优先使用 regexp
        String regexp = ad.getRegexp();
        if (null != regexp && !regexp.isEmpty()) {
            return (e) -> Pattern.matches(regexp, e.toString());
        }

        // 其次使用 testClassName
        String testClassName = ad.getTestClassName();
        if (testClassName != null && !testClassName.isEmpty()) {
            if (STRING_PREDICATE_CACHE.containsKey(testClassName)) {
                return STRING_PREDICATE_CACHE.get(testClassName);
            } else {
                synchronized (STRING_PREDICATE_CACHE) {
                    if (STRING_PREDICATE_CACHE.containsKey(testClassName)) {
                        return STRING_PREDICATE_CACHE.get(testClassName);
                    } else {
                        try {
                            Class<?> clazz = Class.forName(testClassName);
                            Predicate<Object> p = (Predicate<Object>) clazz.getDeclaredConstructor().newInstance();
                            STRING_PREDICATE_CACHE.put(testClassName, p);
                            return p;
                        } catch (Exception e) {
                            throw new ReflectiveOperationException("Predicate class not found: " + testClassName, e);
                        }
                    }
                }
            }
        }

        // 最后使用 testClass
        Class<?> testClass = ad.getTestClass();
        if (testClass != null && testClass != Void.class) {
            if (CLASS_PREDICATE_CACHE.containsKey(testClass)) {
                return CLASS_PREDICATE_CACHE.get(testClass);
            } else {
                synchronized (CLASS_PREDICATE_CACHE) {
                    if (CLASS_PREDICATE_CACHE.containsKey(testClass)) {
                        return CLASS_PREDICATE_CACHE.get(testClass);
                    } else {
                        try {
                            Predicate<Object> p = (Predicate<Object>) testClass.getDeclaredConstructor().newInstance();
                            CLASS_PREDICATE_CACHE.put(testClass, p);
                            return p;
                        } catch (Exception e) {
                            throw new ReflectiveOperationException("Predicate class not found: " + testClass.getName(), e);
                        }
                    }
                }
            }
        }

        // 没有配置时, 均视为有效
        return (e) -> true;
    }

}
