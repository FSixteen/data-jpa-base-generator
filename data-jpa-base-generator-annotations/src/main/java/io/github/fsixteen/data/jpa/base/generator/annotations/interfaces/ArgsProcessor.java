package io.github.fsixteen.data.jpa.base.generator.annotations.interfaces;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Args;

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
     * @param root  见{@link javax.persistence.criteria.Root}.
     * @param query 见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}.
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
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
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
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return Expression&lt;T&gt;
     */
    default <A extends Annotation, T> Expression<T> create(final A anno, final Args arg, final Object obj, final Object fieldName, final Object fieldValue,
        final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return this.create(anno, arg, obj, fieldValue, root, query, cb);
    }

}
