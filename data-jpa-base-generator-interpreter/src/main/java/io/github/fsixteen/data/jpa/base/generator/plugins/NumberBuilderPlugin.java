package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;

/**
 * 有关{@link java.lang.Number}类型计算内容的注解解释器.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class NumberBuilderPlugin extends AbstractComputerBuilderPlugin<Annotation> {

    private ComparableType type = ComparableType.GT;

    public NumberBuilderPlugin(ComparableType type) {
        this.type = type;
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
                    return cb.gt(leftExpression.get(), rightExpression.get());
                case GTE:
                    return cb.ge(leftExpression.get(), rightExpression.get());
                case LT:
                    return cb.lt(leftExpression.get(), rightExpression.get());
                case LTE:
                    return cb.le(leftExpression.get(), rightExpression.get());
                default:
                    return cb.equal(leftExpression.get(), rightExpression.get());
            }
        }
        return null;
    }

}
