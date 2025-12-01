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

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Null;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * 有关空值计算内容的注解解释器.<br>
 *
 * @author FSixteen
 * @since 1.0.2
 */
public class NullBuilderPlugin extends AbstractComputerBuilderPlugin<Null> {

    private static final Logger LOG = LoggerFactory.getLogger(NullBuilderPlugin.class);

    @Override
    public ComputerDescriptor<Null> toPredicate(AnnotationDescriptor<Null> ad, Object obj, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb)
        throws ClassNotFoundException {
        try {
            Object fieldValue = this.getFieldValue(ad, obj);

            if (ad.isRequired() && Objects.isNull(fieldValue)) {
                return this.toNullValuePredicate(ad, root);
            }

            if (!(fieldValue instanceof Boolean) || this.isIgnore(ad, fieldValue, root, query, cb)) {
                return null;
            }

            Optional<Expression<Object>> leftExpression = Optional.ofNullable(this.fieldConvert(ad, obj, fieldValue, root, query, cb))
                .filter(it -> this.checkField(ad, fieldValue, root, query, cb));

            Optional<Predicate> predicateOptional = leftExpression
                .map(it -> Objects.equals(Boolean.TRUE, fieldValue) ? ad.getAnno().whenTrueUse().apply(it) : ad.getAnno().whenFalseUse().apply(it));

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

}
