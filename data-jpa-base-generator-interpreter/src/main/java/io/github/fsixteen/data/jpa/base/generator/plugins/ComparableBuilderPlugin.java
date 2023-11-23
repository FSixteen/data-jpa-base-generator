package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;
import java.util.Optional;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;

/**
 * 有关{@link java.lang.Comparable}类型计算内容的注解解释器.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class ComparableBuilderPlugin extends AbstractComputerBuilderPlugin<Annotation> {

    private ComparableType type = ComparableType.GT;

    public ComparableBuilderPlugin(ComparableType type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isIgnore(AnnotationDescriptor<Annotation> ad, Object fieldValue, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb) {
        return super.isIgnore(ad, fieldValue, root, query, cb) || (String.class.isInstance(fieldValue)
            && ((ad.isIgnoreEmpty() && String.class.cast(fieldValue).isEmpty()) || (ad.isIgnoreBlank() && String.class.cast(fieldValue).trim().isEmpty())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Predicate createPredicate(CriteriaBuilder cb, Optional<Expression> leftExpression, Optional<Expression> rightExpression) {
        if (leftExpression.isPresent() && rightExpression.isPresent()) {
            switch (this.type) {
                case GT:
                    return cb.greaterThan(leftExpression.get(), rightExpression.get());
                case GTE:
                    return cb.greaterThanOrEqualTo(leftExpression.get(), rightExpression.get());
                case LT:
                    return cb.lessThan(leftExpression.get(), rightExpression.get());
                case LTE:
                    return cb.lessThanOrEqualTo(leftExpression.get(), rightExpression.get());
                default:
                    return cb.equal(leftExpression.get(), rightExpression.get());
            }
        }
        return null;
    }

}
