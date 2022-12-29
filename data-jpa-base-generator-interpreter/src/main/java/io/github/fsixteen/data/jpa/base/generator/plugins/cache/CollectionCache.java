package io.github.fsixteen.data.jpa.base.generator.plugins.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.fsixteen.data.jpa.base.generator.plugins.collections.AnnotationCollection;

/**
 * @author FSixteen
 * @since V1.0.0
 */
public final class CollectionCache {

    /** 参数计算字段内容 */
    private static final Map<Class<?>, AnnotationCollection> CONPUTERS = new ConcurrentHashMap<>();

    /**
     * 计算参数计算的内容.<br>
     *
     * @param clazz
     */

    public static AnnotationCollection getAnnotationCollection(Class<?> clazz) {
        if (!CONPUTERS.containsKey(clazz) || CONPUTERS.get(clazz).isEmpty()) {
            synchronized (CollectionCache.class) {
                if (!CONPUTERS.containsKey(clazz) || CONPUTERS.get(clazz).isEmpty()) {
                    CONPUTERS.put(clazz, AnnotationCollection.Builder.of().with(clazz).build());
                }
            }
        }
        return CONPUTERS.get(clazz);
    }

}
