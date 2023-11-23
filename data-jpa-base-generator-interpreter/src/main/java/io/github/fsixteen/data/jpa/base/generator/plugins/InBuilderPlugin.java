package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * 有关包含, 不包含某集合类型计算内容的注解解释器.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public class InBuilderPlugin<A extends Annotation> extends AbstractComputerBuilderPlugin<A> {
    private static final Logger LOG = LoggerFactory.getLogger(InBuilderPlugin.class);

    private ComparableType type = ComparableType.IN;

    public InBuilderPlugin(ComparableType type) {
        this.type = type;
    }

    /**
     * 转换值为 {@linkplain java.util.Collection Collection}.
     * 
     * @param ad         注解描述信息实例.
     * @param fieldValue 待转换的值.
     * @return Collection
     */
    Collection<?> transition(AnnotationDescriptor<A> ad, Object fieldValue) {
        if (Objects.isNull(fieldValue)) {
            return null;
        }
        if (Collection.class.isInstance(fieldValue)) {
            return Collection.class.cast(fieldValue);
        } else if (fieldValue.getClass().isArray()) {
            return Arrays.asList((Object[]) fieldValue);
        } else if (Comparable.class.isInstance(fieldValue)) {
            return Arrays.asList(fieldValue);
        } else {
            return Arrays.asList(fieldValue);
        }
    }

    /**
     * 集合元素过滤条件.<br>
     * 
     * @param ad  注解描述信息实例.
     * @param obj 原实例.
     * @return
     * @throws ReflectiveOperationException
     */
    java.util.function.Predicate<Object> getTestPredicate(AnnotationDescriptor<A> ad, Object obj) throws ReflectiveOperationException {
        return (e) -> true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isIgnore(AnnotationDescriptor<A> ad, Object fieldValue, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb) {
        return super.isIgnore(ad, fieldValue, root, query, cb) || Collection.class.isInstance(fieldValue) && Collection.class.cast(fieldValue).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkFieldValue(AnnotationDescriptor<A> ad, Object fieldValue, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb) {
        switch (ad.getValueType()) {
            case VALUE:
                return Collection.class.isInstance(fieldValue);
            default:
                return super.checkFieldValue(ad, fieldValue, root, query, cb);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public ComputerDescriptor<A> toPredicate(AnnotationDescriptor<A> ad, Object obj, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb) {
        try {
            Collection<?> stap1FieldValue = this.transition(ad, this.getFieldValue(ad, obj));
            List<?> fieldValue = Objects.isNull(stap1FieldValue) ? null
                : stap1FieldValue.stream()
                    .filter(ele -> !(ad.isIgnoreNull() && Objects.isNull(ele)
                        || ad.isIgnoreEmpty() && String.class.isInstance(ele) && String.class.cast(ele).isEmpty()
                        || ad.isIgnoreBlank() && String.class.isInstance(ele) && String.class.cast(ele).trim().isEmpty()))
                    .filter(this.getTestPredicate(ad, obj)).collect(Collectors.toList());

            if (ad.isRequired() && Objects.isNull(fieldValue)) {
                return this.toNullValuePredicate(ad, root);
            }

            if (this.isIgnore(ad, fieldValue, root, query, cb)) {
                return null;
            }

            Optional<Expression> leftExpression = Optional.ofNullable(fieldValue)
                // 值类型判断
                .filter(it -> this.checkField(ad, it, root, query, cb))
                // 值转换
                .map(it -> this.fieldConvert(ad, obj, it, root, query, cb));

            Optional<Expression> rightExpression = Optional.ofNullable(fieldValue)
                // 值类型判断
                .filter(it -> this.checkFieldValue(ad, it, root, query, cb))
                // 值转换
                .map(it -> this.fieldValueConvert(ad, obj, it, root, query, cb));

            Optional<Predicate> predicateOptional = Optional.ofNullable(this.createPredicate(cb, leftExpression, rightExpression));

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

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Predicate createPredicate(CriteriaBuilder cb, Optional<Expression> leftExpression, Optional<Expression> rightExpression) {
        if (leftExpression.isPresent() && rightExpression.isPresent()) {
            switch (this.type) {
                case IN:
                case SPLIT_IN:
                    return leftExpression.get().in(rightExpression.get());
                default:
                    return leftExpression.get().in(rightExpression.get()).not();
            }
        }
        return null;
    }

}
