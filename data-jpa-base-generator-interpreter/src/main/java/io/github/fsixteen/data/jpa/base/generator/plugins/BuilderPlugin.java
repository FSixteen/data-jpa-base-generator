package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.ValueProcessor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * 注解解释器接口.<br>
 * 
 * @author FSixteen
 * @since V1.0.0
 */
public interface BuilderPlugin<AN extends Annotation> {

    static final Map<Class<? extends ValueProcessor>, ValueProcessor> VALUE_PROCESSOR_CACHE = new ConcurrentHashMap<>(32);
    static Logger log = LoggerFactory.getLogger(BuilderPlugin.class);

    /**
     * 接口创建注解逻辑描述信息.<br>
     * 
     * @param ad    注解描述信息实例
     * @param obj   原实例.
     * @param root  见{@link javax.persistence.criteria.Root}.
     * @param query 见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return ComputerDescriptor&lt;AN&gt;
     */
    ComputerDescriptor<AN> toPredicate(AnnotationDescriptor<AN> ad, Object obj, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb);

    /**
     * 创建等于空值的注解逻辑描述信息.<br>
     * 
     * @param ad   注解描述信息实例
     * @param root 见{@link javax.persistence.criteria.Root}.
     * @param cb   见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return ComputerDescriptor&lt;AN&gt;
     */
    default ComputerDescriptor<AN> toNullValuePredicate(final AnnotationDescriptor<AN> ad, final Root<?> root, final CriteriaBuilder cb) {
        return ComputerDescriptor.of(ad, this.logicReverse(ad, root.get(ad.getComputerFieldName()).isNull(), cb));
    }

    /**
     * 值函数执行类执行返回{@link Expression}.<br>
     * 
     * @param <T>   返回计算数据类型
     * @param ad    注解描述信息实例
     * @param obj   原实例.
     * @param root  见{@link javax.persistence.criteria.Root}.
     * @param query 见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return Expression&lt;T&gt;
     * @throws ReflectiveOperationException 值函数执行类实例化异常
     */
    default <T> Expression<T> applyValueProcessor(final AnnotationDescriptor<AN> ad, final Object obj, final Root<?> root, final AbstractQuery<?> query,
            final CriteriaBuilder cb) throws ReflectiveOperationException {
        Class<? extends ValueProcessor> clazz = ad.getValueProcessor().processorClass();
        if (!VALUE_PROCESSOR_CACHE.containsKey(clazz)) {
            VALUE_PROCESSOR_CACHE.put(clazz, clazz.getDeclaredConstructor().newInstance());
        }
        ValueProcessor processor = VALUE_PROCESSOR_CACHE.get(clazz);
        return processor.<AN, T>create(ad.getAnno(), ad.getValueProcessor(), obj, root, query, cb);
    }

    /**
     * 值函数执行类执行返回{@link Expression}.<br>
     * 
     * @param <T>   返回计算数据类型
     * @param ad    注解描述信息实例
     * @param obj   原实例.
     * @param root  见{@link javax.persistence.criteria.Root}.
     * @param query 见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return Expression&lt;T&gt;[]
     * @throws ReflectiveOperationException 值函数执行类实例化异常
     */
    default <T> Expression<T>[] applyBiValueProcessor(final AnnotationDescriptor<AN> ad, final Object obj, final Root<?> root, final AbstractQuery<?> query,
            final CriteriaBuilder cb) throws ReflectiveOperationException {
        Class<? extends ValueProcessor> clazz = ad.getValueProcessor().processorClass();
        if (!VALUE_PROCESSOR_CACHE.containsKey(clazz)) {
            VALUE_PROCESSOR_CACHE.put(clazz, clazz.getDeclaredConstructor().newInstance());
        }
        ValueProcessor processor = VALUE_PROCESSOR_CACHE.get(clazz);
        return processor.<AN, T>biCreate(ad.getAnno(), ad.getValueProcessor(), obj, root, query, cb);
    }

    /**
     * 按需逻辑反转(If necessary, create a negation of the given restriction).
     *
     * @param ad        注解描述信息实例
     * @param predicate 见{@link javax.persistence.criteria.Predicate}
     * @param cb        见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return Predicate
     */
    default Predicate logicReverse(final AnnotationDescriptor<AN> ad, final Predicate predicate, final CriteriaBuilder cb) {
        return Objects.isNull(predicate) || !ad.isNot() ? predicate : cb.not(predicate);
    }

    /**
     * 支持字符串两端空白字符.<br>
     * 
     * @param ad  注解描述信息实例
     * @param obj 原实例.
     * @see java.lang.String#trim()
     * @return Object
     */
    default Object trimIfPresent(final AnnotationDescriptor<AN> ad, final Object obj) {
        return obj instanceof String && ad.isTrim() ? String.class.cast(obj).trim() : obj;
    }

    /**
     * 打印日志.<br>
     * *
     * 
     * @param ad 注解描述信息实例
     */
    default void printWarn(AnnotationDescriptor<AN> ad) {
        if (log.isDebugEnabled()) {
            log.debug("无效 {} 计算内容, 参与计算字段名 {}, 参与计算值字段名 {}, 类型分别为 {}, {}, 不满足计算条件, 已放弃该条件.", ad.getAnno().annotationType().getSimpleName(),
                    ad.getComputerFieldName(), ad.getValueFieldName(), ad.getComputerField().getType().getSimpleName(),
                    ad.getValueField().getType().getSimpleName());
        }
    }

}
