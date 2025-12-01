package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.util.Objects;
import java.util.Optional;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.IsNull;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * 有关空值计算内容的注解解释器.<br>
 *
 * @author FSixteen
 * @since 1.0.2
 */
public class IsNullBuilderPlugin extends AbstractComputerBuilderPlugin<IsNull> {

    private static final Logger LOG = LoggerFactory.getLogger(IsNullBuilderPlugin.class);

    @Override
    @SuppressWarnings("rawtypes")
    public ComputerDescriptor<IsNull> toPredicate(AnnotationDescriptor<IsNull> ad, Object obj, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb)
        throws ClassNotFoundException {
        try {
            Object fieldValue = this.getFieldValue(ad, obj);

            if (ad.isRequired() && Objects.isNull(fieldValue)) {
                return this.toNullValuePredicate(ad, root);
            }

            if (!Objects.equals(Boolean.TRUE, fieldValue) || this.isIgnore(ad, fieldValue, root, query, cb)) {
                return null;
            }

            Optional<Expression> leftExpression = Optional.ofNullable(fieldValue)
                // 值类型判断
                .filter(it -> this.checkField(ad, it, root, query, cb))
                // 值转换
                .map(it -> this.fieldConvert(ad, obj, it, root, query, cb));

            Optional<Predicate> predicateOptional = Optional.ofNullable(this.createPredicate(cb, leftExpression, null));

            if (predicateOptional.isPresent()) {
                return ComputerDescriptor.of(ad, this.logicReverse(ad.isNot(), predicateOptional.get()));
            } else {
                this.printWarn(ad, root);
            }
        } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected Predicate createPredicate(CriteriaBuilder cb, Optional<Expression> leftExpression, Optional<Expression> rightExpression) {
        return leftExpression.get().isNull();
    }

}
