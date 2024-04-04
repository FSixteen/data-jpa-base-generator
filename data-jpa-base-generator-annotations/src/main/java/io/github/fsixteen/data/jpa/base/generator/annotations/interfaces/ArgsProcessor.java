package io.github.fsixteen.data.jpa.base.generator.annotations.interfaces;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Args;

import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

/**
 * 自定义函数参数处理器.<br>
 *
 * @author FSixteen
 * @since 1.0.2
 */
public interface ArgsProcessor extends Serializable {

    /**
     * 自定义函数参数处理器.<br>
     * 
     * @param <A>   Annotation类型.
     * @param <T>   数据类型.
     * @param anno  Annotation实例.
     * @param arg   Args实例.
     * @param obj   原实例.
     * @param root  见{@link jakarta.persistence.criteria.Root}.
     * @param query 见{@link jakarta.persistence.criteria.AbstractQuery}.
     * @param cb    见{@link jakarta.persistence.criteria.CriteriaBuilder}.
     * @return Expression&lt;T&gt;
     */
    default <A extends Annotation, T> Expression<T> create(final A anno, final Args arg, final Object obj, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb) {
        return null;
    }

    /**
     * 自定义函数参数处理器.<br>
     * 
     * @param <A>        Annotation类型.
     * @param <T>        数据类型.
     * @param anno       Annotation实例.
     * @param arg        Args实例.
     * @param obj        原实例.
     * @param fieldValue 当前值.
     * @param root       见{@link jakarta.persistence.criteria.Root}.
     * @param query      见{@link jakarta.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link jakarta.persistence.criteria.CriteriaBuilder}.
     * @return Expression&lt;T&gt;
     */
    default <A extends Annotation, T> Expression<T> create(final A anno, final Args arg, final Object obj, final Object fieldValue, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return this.create(anno, arg, obj, root, query, cb);
    }

    /**
     * 自定义函数参数处理器.<br>
     * 
     * @param <A>        Annotation类型.
     * @param <T>        数据类型.
     * @param anno       Annotation实例.
     * @param arg        Args实例.
     * @param obj        原实例.
     * @param fieldName  当前字段.
     * @param fieldValue 当前值.
     * @param root       见{@link jakarta.persistence.criteria.Root}.
     * @param query      见{@link jakarta.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link jakarta.persistence.criteria.CriteriaBuilder}.
     * @return Expression&lt;T&gt;
     */
    default <A extends Annotation, T> Expression<T> create(final A anno, final Args arg, final Object obj, final Object fieldName, final Object fieldValue,
        final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return this.create(anno, arg, obj, fieldValue, root, query, cb);
    }

}
