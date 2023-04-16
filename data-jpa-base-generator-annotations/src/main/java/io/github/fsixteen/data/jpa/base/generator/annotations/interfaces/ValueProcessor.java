package io.github.fsixteen.data.jpa.base.generator.annotations.interfaces;

import java.lang.annotation.Annotation;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ValueProcessorFunction;

/**
 * 值处理器.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
public interface ValueProcessor {

    /**
     * 单值处理器.<br>
     * 
     * @param <AN>  Annotation类型.
     * @param <T>   数据类型.
     * @param anno  Annotation实例.
     * @param fun   ValueProcessorFunction实例.
     * @param obj   原实例.
     * @param root  见{@link javax.persistence.criteria.Root}.
     * @param query 见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return Expression&lt;T&gt;
     */
    <AN extends Annotation, T> Expression<T> create(final AN anno, final ValueProcessorFunction fun, final Object obj, final Root<?> root,
            final AbstractQuery<?> query, final CriteriaBuilder cb);

    /**
     * 双值处理器.<br>
     * 
     * @param <AN>  Annotation类型.
     * @param <T>   数据类型.
     * @param anno  Annotation实例.
     * @param fun   ValueProcessorFunction实例.
     * @param obj   原实例.
     * @param root  见{@link javax.persistence.criteria.Root}.
     * @param query 见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return Expression&lt;T&gt;[]
     */
    <AN extends Annotation, T> Expression<T>[] biCreate(final AN anno, final ValueProcessorFunction fun, final Object obj, final Root<?> root,
            final AbstractQuery<?> query, final CriteriaBuilder cb);

}
