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
 * @since 1.0.0
 */
public class DefaultValueProcessor implements ValueProcessor {
    private static final long serialVersionUID = 1L;

    @Override
    public <A extends Annotation, T> Expression<T> create(A anno, ValueProcessorFunction fun, Object obj, Root<?> root, AbstractQuery<?> query,
        CriteriaBuilder cb) {
        return null;
    }

    @Override
    public <A extends Annotation, T> Expression<T>[] biCreate(A anno, ValueProcessorFunction fun, Object obj, Root<?> root, AbstractQuery<?> query,
        CriteriaBuilder cb) {
        return null;
    }

}
