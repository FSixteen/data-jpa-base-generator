package io.github.fsixteen.data.jpa.base.generator.annotations.interfaces;

import java.lang.annotation.Annotation;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ValueProcessorFunction;

/**
 * 默认值处理器.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
public class DefaultValueProcessor implements ValueProcessor {

    @Override
    public <AN extends Annotation, T> Expression<T> create(final AN anno, final ValueProcessorFunction fun, final Object obj, final Root<?> root,
            final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return null;
    }

    @Override
    public <AN extends Annotation, T> Expression<T>[] biCreate(final AN anno, final ValueProcessorFunction fun, final Object obj, final Root<?> root,
            final AbstractQuery<?> query, final CriteriaBuilder cb) {
        return null;
    }

}
