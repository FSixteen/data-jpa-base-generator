package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Function;
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
        } else {
            return Arrays.asList(fieldValue);
        }
    }

    /**
     * 集合元素过滤条件.<br>
     * 
     * @param ad  注解描述信息实例.
     * @param obj 原实例.
     * @return java.util.function.Predicate&lt;Object&gt;
     * @throws ReflectiveOperationException Common superclass of exceptions thrown
     *                                      by reflective operations in core
     *                                      reflection.
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
            case LITERAL_BOOLEAN:
                return false;
            case LITERAL_BIGDECIMAL:
            case LITERAL_DOUBLE:
            case LITERAL_FLOAT:
            case LITERAL_SHORT:
            case LITERAL_INTEGER:
            case LITERAL_LONG:
                return !"".equals(ad.getValueLiteral()) && Pattern.matches("^(\\-|\\+)?\\d+(\\.\\d+)?(,((\\-|\\+)?\\d+(\\.\\d+)?)*)?$", ad.getValueLiteral());
            case VALUE:
                return Collection.class.isInstance(fieldValue);
            default:
                return super.checkFieldValue(ad, fieldValue, root, query, cb);
        }
    }

    /**
     * 解析字面量值
     * 
     * @param valueLiteral 字面量字符串
     * @return 解析后的值数组
     */
    private List<String> parseLiteralValues(String valueLiteral, String decollator) {
        StringTokenizer tokenizer = new StringTokenizer(valueLiteral, decollator);
        ArrayList<String> values = new ArrayList<>();
        while (tokenizer.hasMoreElements())
            values.add(tokenizer.nextToken());
        return values;
    }

    /**
     * 处理字面量类型的值转换
     * 
     * @param ad 注解描述信息实例
     * @param cb CriteriaBuilder实例
     * @return Expression数组
     */
    private Expression<Collection<?>> handleLiteralValueTypes(AnnotationDescriptor<A> ad, CriteriaBuilder cb) {
        List<String> values = this.parseLiteralValues(ad.getValueLiteral(), ad.getDecollator());
        switch (ad.getValueType()) {
            case LITERAL:
                return cb.literal(values);
            case LITERAL_BIGDECIMAL:
                return cb.literal(values.stream().map(BigDecimal::new).collect(Collectors.toList()));
            case LITERAL_DOUBLE:
                return cb.literal(values.stream().map(Double::valueOf).collect(Collectors.toList()));
            case LITERAL_FLOAT:
                return cb.literal(values.stream().map(Float::valueOf).collect(Collectors.toList()));
            case LITERAL_SHORT:
                return cb.literal(values.stream().map(Short::valueOf).collect(Collectors.toList()));
            case LITERAL_INTEGER:
                return cb.literal(values.stream().map(Integer::valueOf).collect(Collectors.toList()));
            case LITERAL_LONG:
                return cb.literal(values.stream().map(Long::valueOf).collect(Collectors.toList()));
            default:
                return null;
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
    @SuppressWarnings("unchecked")
    private Expression<Collection<?>> fieldValueConverts(AnnotationDescriptor<A> ad, Object obj, Collection<?> fieldValue, Root<?> root, AbstractQuery<?> query,
        CriteriaBuilder cb) {
        switch (ad.getValueType()) {
            case AUTO:
                return cb.literal(fieldValue);
            case LITERAL:
            case LITERAL_BIGDECIMAL:
            case LITERAL_DOUBLE:
            case LITERAL_FLOAT:
            case LITERAL_SHORT:
            case LITERAL_INTEGER:
            case LITERAL_LONG:
                return this.handleLiteralValueTypes(ad, cb);
            case VALUE:
                return cb.literal(fieldValue);
            case COLUMN:
                LOG.warn("InBuilderPlugin 中, 不支持 valueType == ValueType.COLUMN.");
                return null;
            case FUNCTION:
                Function function = ad.getValueFunction();
                return cb.<Collection<?>>function(function.value(), (Class<Collection<?>>) function.type(),
                    this.createFunctionExpression(ad, function, obj, fieldValue, root, query, cb));
            case UDFUNCTION:
                try {
                    return this.applyValueProcessor(ad, obj, fieldValue, root, query, cb);
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
                .map(it -> this.fieldValueConverts(ad, obj, it, root, query, cb));

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
