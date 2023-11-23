package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Between;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Function;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * 范围条件.<br>
 * {@link io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Between}注解解释器.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public class BetweenBuilderPlugin extends AbstractComputerBuilderPlugin<Between> {
    private static final Logger LOG = LoggerFactory.getLogger(BetweenBuilderPlugin.class);

    /**
     * 转换值为 {@linkplain java.util.Collection Collection}.
     * 
     * @param ad         注解描述信息实例.
     * @param fieldValue 待转换的值.
     * @return Collection
     */
    Collection<?> transition(AnnotationDescriptor<Between> ad, Object fieldValue) {
        if (Objects.isNull(fieldValue)) {
            return null;
        }
        if (Collection.class.isInstance(fieldValue) && 2 == Collection.class.cast(fieldValue).size()) {
            return Collection.class.cast(fieldValue);
        } else if (fieldValue.getClass().isArray() && 2 == ((Object[]) fieldValue).length) {
            return Arrays.asList((Object[]) fieldValue);
        } else {
            LOG.warn("实例 {} 请求参数 {} {} 计算逻辑, 传入参数异常 [ {} ], 当前仅支持 java.lang.Comparable[2] 或 java.util.Collection<java.lang.Comparable>[2]", ad.getObjClass(),
                ad.getValueFieldName(), ad.getAnno().getClass(), fieldValue);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isIgnore(AnnotationDescriptor<Between> ad, Object fieldValue, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb) {
        return super.isIgnore(ad, fieldValue, root, query, cb);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkFieldValue(AnnotationDescriptor<Between> ad, Object fieldValue, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb) {
        switch (ad.getValueType()) {
            case VALUE:
                return Collection.class.isInstance(fieldValue);
            default:
                return super.checkFieldValue(ad, fieldValue, root, query, cb);
        }
    }

    /**
     * 转换当前值.
     * 
     * @param ad         注解描述信息实例.
     * @param obj        原实例.
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return boolean
     */
    private Expression<?>[] fieldValueConverts(AnnotationDescriptor<Between> ad, Object obj, Collection<?> fieldValue, Root<?> root, AbstractQuery<?> query,
        CriteriaBuilder cb) {
        List<?> _fieldValue = List.class.isInstance(fieldValue) ? (List<?>) fieldValue : new ArrayList<>(fieldValue);
        switch (ad.getValueType()) {
            case VALUE:
                return new Expression<?>[] { cb.literal(_fieldValue.get(0)), cb.literal(_fieldValue.get(1)) };
            case COLUMN:
                return new Expression<?>[] { root.get(String.class.cast(_fieldValue.get(0))), root.get(String.class.cast(_fieldValue.get(1))) };
            case FUNCTION:
                Function function = ad.getValueFunction();
                return new Expression<?>[] {
                    cb.function(function.value(), function.type(), this.creaateFunctionExpression(ad, function, obj, _fieldValue.get(0), root, query, cb)),
                    cb.function(function.value(), function.type(), this.creaateFunctionExpression(ad, function, obj, _fieldValue.get(1), root, query, cb)) };
            case UDFUNCTION:
                try {
                    return this.applyBiValueProcessor(ad, obj, _fieldValue, root, query, cb);
                } catch (ReflectiveOperationException e) {
                    LOG.error(e.getMessage(), e);
                    return null;
                }
            default:
                return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "rawtypes" })
    public ComputerDescriptor<Between> toPredicate(AnnotationDescriptor<Between> ad, Object obj, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb) {
        try {
            Collection<?> fieldValue = this.transition(ad, this.getFieldValue(ad, obj));

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
                .map(it -> this.<Comparable>fieldConvert(ad, obj, it, root, query, cb));

            Optional<Expression[]> rightExpression = Optional.ofNullable(fieldValue)
                // 值类型判断
                .filter(it -> this.checkFieldValue(ad, it, root, query, cb))
                // 值转换
                .map(it -> this.fieldValueConverts(ad, obj, it, root, query, cb));

            Optional<Predicate> predicateOptional = Optional.ofNullable(this.createPredicates(cb, leftExpression, rightExpression));

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
     * 创建 {@link javax.persistence.criteria.Predicate} 谓词.
     * 
     * @param cb              见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @param leftExpression  表达式1
     * @param rightExpression 表达式2
     * @return Predicate
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Predicate createPredicates(CriteriaBuilder cb, Optional<Expression> leftExpression, Optional<Expression[]> rightExpression) {
        if (leftExpression.isPresent() && rightExpression.isPresent()) {
            return cb.between(leftExpression.get(), rightExpression.get()[0], rightExpression.get()[1]);
        }
        return null;
    }

}
