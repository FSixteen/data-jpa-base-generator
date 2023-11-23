package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.DefaultFieldProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.DefaultValueProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.FieldProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.ValueProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.FieldProcessorFunction;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ValueProcessorFunction;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * 注解解释器接口.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public interface BuilderPlugin<A extends Annotation> {

    static final Map<Object, FieldProcessor> FIELD_PROCESSOR_CACHE = new ConcurrentHashMap<>(1 << 6);
    static final Map<Object, ValueProcessor> VALUE_PROCESSOR_CACHE = new ConcurrentHashMap<>(1 << 6);
    static Logger LOG = LoggerFactory.getLogger(BuilderPlugin.class);

    /**
     * 接口创建注解逻辑描述信息.<br>
     * 
     * @param ad    注解描述信息实例.
     * @param obj   原实例.
     * @param root  见{@link javax.persistence.criteria.Root}.
     * @param query 见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return ComputerDescriptor&lt;A&gt;
     * @throws ClassNotFoundException if the class cannot be located
     */
    ComputerDescriptor<A> toPredicate(AnnotationDescriptor<A> ad, Object obj, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb)
        throws ClassNotFoundException;

    /**
     * 校验字段(列)处理器实现类.
     * 
     * @param fieldProcessorFunction 字段(列)函数执行信息.
     * @return boolean true: 可用, false: 不可用
     * @since 1.0.2
     */
    default boolean checkFieldProcessor(final FieldProcessorFunction fieldProcessorFunction) {
        if (fieldProcessorFunction.processorClassName().isEmpty() && DefaultFieldProcessor.class == fieldProcessorFunction.processorClass()) {
            // 无效配置
            return false;
        }
        // 可能有效配置
        try {
            // 首先尝试 processorClassName
            final String processorClassName = fieldProcessorFunction.processorClassName();
            if (!processorClassName.isEmpty()) {
                // processorClassName 有效
                if (FIELD_PROCESSOR_CACHE.containsKey(processorClassName)) {
                    // 曾处理过
                    return true;
                }
                synchronized (FIELD_PROCESSOR_CACHE) {
                    if (FIELD_PROCESSOR_CACHE.containsKey(processorClassName)) {
                        return true;
                    }
                    Class<?> clazz = Class.forName(processorClassName);
                    FIELD_PROCESSOR_CACHE.put(processorClassName, (FieldProcessor) clazz.getDeclaredConstructor().newInstance());
                }
                return true;
            }
        } catch (ReflectiveOperationException e) {
            LOG.error(e.getMessage(), e);
            // 反射失败, 继续执行, 尝试 processorClass
        }
        try {
            // 再次尝试 processorClass
            Class<?> processorClass = fieldProcessorFunction.processorClass();
            if (DefaultValueProcessor.class != processorClass) {
                // processorClass 有效
                if (FIELD_PROCESSOR_CACHE.containsKey(processorClass)) {
                    // 曾处理过
                    return true;
                }
                synchronized (FIELD_PROCESSOR_CACHE) {
                    if (FIELD_PROCESSOR_CACHE.containsKey(processorClass)) {
                        return true;
                    }
                    FIELD_PROCESSOR_CACHE.put(processorClass, (FieldProcessor) processorClass.getDeclaredConstructor().newInstance());
                }
                return true;
            }
        } catch (ReflectiveOperationException e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 获取字段(列)处理器实现类.
     * 
     * @param fieldProcessorFunction 值函数执行信息.
     * @return {@linkplain io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.FieldProcessor
     *         FieldProcessor}
     * @since 1.0.2
     */
    default FieldProcessor getFieldProcessor(final FieldProcessorFunction fieldProcessorFunction) {
        this.checkFieldProcessor(fieldProcessorFunction);
        return FIELD_PROCESSOR_CACHE.getOrDefault(fieldProcessorFunction.processorClassName(),
            FIELD_PROCESSOR_CACHE.get(fieldProcessorFunction.processorClass()));
    }

    /**
     * 字段(列)处理器实现类执行返回 {@linkplain javax.persistence.criteria.Expression
     * Expression}.
     * 
     * @param <T>        返回计算数据类型
     * @param ad         注解描述信息实例
     * @param obj        原实例.
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return Expression&lt;T&gt;
     * @throws ReflectiveOperationException 值函数执行类实例化异常
     * @since 1.0.2
     */
    default <T> Expression<T> applyFieldProcessor(final AnnotationDescriptor<A> ad, final Object obj, final Object fieldValue, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) throws ReflectiveOperationException {
        return this.applyFieldProcessor(ad, obj, null, fieldValue, root, query, cb);
    }

    /**
     * 字段(列)处理器实现类执行返回 {@linkplain javax.persistence.criteria.Expression
     * Expression}.
     * 
     * @param <T>        返回计算数据类型
     * @param ad         注解描述信息实例
     * @param obj        原实例.
     * @param fieldName  当前字段.
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return Expression&lt;T&gt;
     * @throws ReflectiveOperationException 值函数执行类实例化异常
     */
    default <T> Expression<T> applyFieldProcessor(final AnnotationDescriptor<A> ad, final Object obj, final Object fieldName, final Object fieldValue,
        final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) throws ReflectiveOperationException {
        FieldProcessor processor = this.getFieldProcessor(ad.getFieldProcessor());
        return processor.<A, T>create(ad.getAnno(), ad.getFieldProcessor(), obj, fieldName, fieldValue, root, query, cb);
    }

    /**
     * 校验值处理器实现类.
     * 
     * @param valueProcessorFunction 值函数执行信息.
     * @return boolean true: 可用, false: 不可用
     */
    default boolean checkValueProcessor(final ValueProcessorFunction valueProcessorFunction) {
        if (valueProcessorFunction.processorClassName().isEmpty() && DefaultValueProcessor.class == valueProcessorFunction.processorClass()) {
            // 无效配置
            return false;
        }
        // 可能有效配置
        try {
            // 首先尝试 processorClassName
            final String processorClassName = valueProcessorFunction.processorClassName();
            if (!processorClassName.isEmpty()) {
                // processorClassName 有效
                if (VALUE_PROCESSOR_CACHE.containsKey(processorClassName)) {
                    // 曾处理过
                    return true;
                }
                synchronized (VALUE_PROCESSOR_CACHE) {
                    if (VALUE_PROCESSOR_CACHE.containsKey(processorClassName)) {
                        return true;
                    }
                    Class<?> clazz = Class.forName(processorClassName);
                    VALUE_PROCESSOR_CACHE.put(processorClassName, (ValueProcessor) clazz.getDeclaredConstructor().newInstance());
                }
                return true;
            }
        } catch (ReflectiveOperationException e) {
            LOG.error(e.getMessage(), e);
            // 反射失败, 继续执行, 尝试 processorClass
        }
        try {
            // 再次尝试 processorClass
            Class<?> processorClass = valueProcessorFunction.processorClass();
            if (DefaultValueProcessor.class != processorClass) {
                // processorClass 有效
                if (VALUE_PROCESSOR_CACHE.containsKey(processorClass)) {
                    // 曾处理过
                    return true;
                }
                synchronized (VALUE_PROCESSOR_CACHE) {
                    if (VALUE_PROCESSOR_CACHE.containsKey(processorClass)) {
                        return true;
                    }
                    VALUE_PROCESSOR_CACHE.put(processorClass, (ValueProcessor) processorClass.getDeclaredConstructor().newInstance());
                }
                return true;
            }
        } catch (ReflectiveOperationException e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 获取值处理器实现类.
     * 
     * @param valueProcessorFunction 值函数执行信息.
     * @return {@linkplain io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.ValueProcessor
     *         ValueProcessor}
     */
    default ValueProcessor getValueProcessor(final ValueProcessorFunction valueProcessorFunction) {
        this.checkValueProcessor(valueProcessorFunction);
        return VALUE_PROCESSOR_CACHE.getOrDefault(valueProcessorFunction.processorClassName(),
            VALUE_PROCESSOR_CACHE.get(valueProcessorFunction.processorClass()));
    }

    /**
     * 值函数执行类执行返回 {@linkplain javax.persistence.criteria.Expression
     * Expression}.
     * 
     * @param <T>        返回计算数据类型
     * @param ad         注解描述信息实例
     * @param obj        原实例.
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return Expression&lt;T&gt;
     * @throws ReflectiveOperationException 值函数执行类实例化异常
     */
    default <T> Expression<T> applyValueProcessor(final AnnotationDescriptor<A> ad, final Object obj, final Object fieldValue, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) throws ReflectiveOperationException {
        return this.applyValueProcessor(ad, obj, null, fieldValue, root, query, cb);
    }

    /**
     * 值函数执行类执行返回 {@linkplain javax.persistence.criteria.Expression
     * Expression}.
     * 
     * @param <T>        返回计算数据类型
     * @param ad         注解描述信息实例
     * @param obj        原实例.
     * @param fieldName  当前字段.
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return Expression&lt;T&gt;
     * @throws ReflectiveOperationException 值函数执行类实例化异常
     */
    default <T> Expression<T> applyValueProcessor(final AnnotationDescriptor<A> ad, final Object obj, final Object fieldName, final Object fieldValue,
        final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) throws ReflectiveOperationException {
        ValueProcessor processor = this.getValueProcessor(ad.getValueProcessor());
        return processor.<A, T>create(ad.getAnno(), ad.getValueProcessor(), obj, fieldName, fieldValue, root, query, cb);

    }

    /**
     * 值函数执行类执行返回 {@linkplain javax.persistence.criteria.Expression
     * Expression}.
     * 
     * @param <T>        返回计算数据类型
     * @param ad         注解描述信息实例
     * @param obj        原实例.
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return Expression&lt;T&gt;[]
     * @throws ReflectiveOperationException 值函数执行类实例化异常
     */
    default <T> Expression<T>[] applyBiValueProcessor(final AnnotationDescriptor<A> ad, final Object obj, final Object fieldValue, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) throws ReflectiveOperationException {
        return this.applyBiValueProcessor(ad, obj, null, fieldValue, root, query, cb);
    }

    /**
     * 值函数执行类执行返回 {@linkplain javax.persistence.criteria.Expression
     * Expression}.
     * 
     * @param <T>        返回计算数据类型
     * @param ad         注解描述信息实例
     * @param obj        原实例.
     * @param fieldName  当前字段.
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return Expression&lt;T&gt;[]
     * @throws ReflectiveOperationException 值函数执行类实例化异常
     */
    default <T> Expression<T>[] applyBiValueProcessor(final AnnotationDescriptor<A> ad, final Object obj, final Object fieldName, final Object fieldValue,
        final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) throws ReflectiveOperationException {
        ValueProcessor processor = this.getValueProcessor(ad.getValueProcessor());
        return processor.<A, T>biCreate(ad.getAnno(), ad.getValueProcessor(), obj, fieldName, fieldValue, root, query, cb);
    }

    /**
     * 获取当前值.
     * 
     * @param ad  注解描述信息实例.
     * @param obj 原实例.
     * @return Object
     * @throws ReflectiveOperationException 反射取值异常
     */
    default Object getFieldValue(final AnnotationDescriptor<A> ad, final Object obj) throws ReflectiveOperationException {
        return this.trimIfPresent(ad, ad.getValueFieldPd().getReadMethod().invoke(obj));
    }

    /**
     * 逻辑忽略状态.
     * 
     * @param ad         注解描述信息实例
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return boolean
     */
    default boolean isIgnore(final AnnotationDescriptor<A> ad, final Object fieldValue, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb) {
        return ad.isIgnoreNull() && Objects.isNull(fieldValue);
    }

    /**
     * 删除所有前导和尾随空白字符.<br>
     * 
     * @param ad  注解描述信息实例
     * @param obj 原实例.
     * @see java.lang.String#trim()
     * @return Object
     */
    default Object trimIfPresent(final AnnotationDescriptor<A> ad, final Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        if (ad.isTrim()) {
            if (obj instanceof String) {
                return String.class.cast(obj).trim();
            }
            List<Object> objs = new ArrayList<>();
            if (obj instanceof Collection) {
                for (Object o : Collection.class.cast(obj)) {
                    objs.add(o instanceof String ? String.class.cast(o).trim() : o);
                }
                return objs;
            } else if (obj.getClass().isArray()) {
                for (Object o : (Object[]) obj) {
                    objs.add(o instanceof String ? String.class.cast(o).trim() : o);
                }
                return objs;
            }
        }
        return obj;
    }

    /**
     * 按需逻辑反转(If necessary, create a negation of the given restriction).
     *
     * @param isNot     逻辑反转开关
     * @param predicate 见{@link javax.persistence.criteria.Predicate}.
     * @return Predicate
     */
    default Predicate logicReverse(final boolean isNot, final Predicate predicate) {
        return isNot && Objects.nonNull(predicate) ? predicate.not() : predicate;
    }

    /**
     * 创建等于空值的注解逻辑描述信息.<br>
     * 
     * @param ad   注解描述信息实例
     * @param root 见{@link javax.persistence.criteria.Root}.
     * @return ComputerDescriptor&lt;A&gt;
     */
    default ComputerDescriptor<A> toNullValuePredicate(final AnnotationDescriptor<A> ad, final Root<?> root) {
        return ComputerDescriptor.of(ad, this.logicReverse(ad.isNot(), root.get(ad.getComputerFieldName()).isNull()));
    }

    /**
     * 打印日志.<br>
     * 
     * @param ad   注解描述信息实例
     * @param root 见{@link javax.persistence.criteria.Root}.
     */
    default void printWarn(final AnnotationDescriptor<A> ad, final Root<?> root) {
        if (LOG.isWarnEnabled()) {
            LOG.warn("无效 {} 计算内容, 参与计算字段名 {}, 参与计算值字段名 {}, 类型分别为 {}, {}, 不满足计算条件, 已放弃该条件.", ad.getAnno().annotationType().getSimpleName(),
                ad.getComputerFieldName(), ad.getValueFieldName(), root.getModel().getAttribute(ad.getComputerFieldName()).getJavaType().getSimpleName(),
                ad.getValueField().getType().getSimpleName());
        }
    }

}
