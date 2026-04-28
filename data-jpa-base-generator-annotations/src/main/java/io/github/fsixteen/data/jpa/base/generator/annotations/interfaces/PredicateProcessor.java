package io.github.fsixteen.data.jpa.base.generator.annotations.interfaces;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Case;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseThen;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseWhen;

/**
 * Predicate处理器.<br>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public interface PredicateProcessor extends Serializable {

    /**
     * Predicate处理器.<br>
     * 
     * @param <A>   Annotation类型.
     * @param anno  Annotation实例.
     * @param cs    Case实例.
     * @param when  CaseWhen实例.
     * @param then  CaseThen实例.
     * @param obj   原实例.
     * @param root  见{@link javax.persistence.criteria.Root}.
     * @param query 见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return Expression&lt;T&gt;
     */
    default <A extends Annotation> Predicate create(final A anno, final Case cs, final CaseWhen when, final CaseThen then, final Object obj, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return null;
    }

    /**
     * Predicate处理器.<br>
     * 
     * @param <A>        Annotation类型.
     * @param anno       Annotation实例.
     * @param cs         Case实例.
     * @param when       CaseWhen实例.
     * @param then       CaseThen实例.
     * @param obj        原实例.
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * 
     * @return Predicate
     */
    default <A extends Annotation> Predicate create(final A anno, final Case cs, final CaseWhen when, final CaseThen then, final Object obj,
        final Object fieldValue, final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return this.create(anno, cs, when, then, obj, root, query, cb);
    }

    /**
     * Predicate处理器.<br>
     * 
     * @param <A>        Annotation类型.
     * @param anno       Annotation实例.
     * @param cs         Case实例.
     * @param when       CaseWhen实例.
     * @param then       CaseThen实例.
     * @param obj        原实例.
     * @param fieldName  当前字段.
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * 
     * @return Predicate
     */
    default <A extends Annotation> Predicate create(final A anno, final Case cs, final CaseWhen when, final CaseThen then, final Object obj,
        final String fieldName, final Object fieldValue, final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return this.create(anno, cs, when, then, obj, fieldValue, root, query, cb);
    }

}
