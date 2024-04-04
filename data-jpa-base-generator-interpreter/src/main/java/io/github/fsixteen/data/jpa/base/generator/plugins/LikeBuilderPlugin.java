package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;
import java.util.Optional;

import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;

import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * 有关{@link java.lang.String}类型计算内容的相似注解解释器.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class LikeBuilderPlugin extends AbstractComputerBuilderPlugin<Annotation> {

    private ComparableType type = ComparableType.CONTAINS;

    public LikeBuilderPlugin(ComparableType type) {
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
                case LEFT:
                case START_WITH:
                    return cb.like(leftExpression.get(), cb.concat(rightExpression.get(), "%"));
                case RIGHT:
                case END_WITH:
                    return cb.like(leftExpression.get(), cb.concat("%", rightExpression.get()));
                case CONTAINS:
                default:
                    return cb.like(leftExpression.get(), cb.concat("%", cb.concat(rightExpression.get(), "%")));
            }
        }
        return null;
    }

}
