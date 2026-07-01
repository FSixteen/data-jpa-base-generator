package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Between;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Functions;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
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

    private final ComparableType type;

    public BetweenBuilderPlugin(ComparableType type) {
        this.type = type;
    }

    /**
     * 转换值为 {@linkplain java.util.Collection Collection}.
     * 
     * @param ad         注解描述信息实例.
     * @param fieldValue 待转换的值.
     * @return Collection
     */
    private Collection<?> transition(final AnnotationDescriptor<Between> ad, final Object fieldValue) {
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
    public boolean checkFieldValue(AnnotationDescriptor<Between> ad, Object fieldValue, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb) {
        switch (ad.getValueType()) {
            case LITERAL_BOOLEAN:
                return false;
            case LITERAL_BIGDECIMAL:
            case LITERAL_DOUBLE:
            case LITERAL_FLOAT:
            case LITERAL_SHORT:
            case LITERAL_INTEGER:
            case LITERAL_LONG:
                return !"".equals(ad.getValueLiteral()) && Pattern.matches("^(\\-|\\+)?\\d+(\\.\\d+)?(,(\\-|\\+)?\\d+(\\.\\d+)?)?$", ad.getValueLiteral());
            case LITERAL_COLUMN:
                return ad.getValueLiteral().contains(",") && !ad.getValueLiteral().startsWith(",") && !ad.getValueLiteral().endsWith(",");
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
    private String[] parseLiteralValues(String valueLiteral) {
        String[] values = new String[2];
        StringTokenizer tokenizer = new StringTokenizer(valueLiteral, Constant.DECOLLATOR);
        if (tokenizer.hasMoreElements()) {
            values[0] = tokenizer.nextToken();
        }
        if (tokenizer.hasMoreElements()) {
            values[1] = tokenizer.nextToken();
        } else {
            values[1] = values[0];
        }
        return values;
    }

    /**
     * 处理字面量类型的值转换
     * 
     * @param ad 注解描述信息实例
     * @param cb CriteriaBuilder实例
     * @return Expression数组
     */
    private Expression<?>[] handleLiteralValueTypes(AnnotationDescriptor<Between> ad, CriteriaBuilder cb) {
        String[] values = this.parseLiteralValues(ad.getValueLiteral());
        switch (ad.getValueType()) {
            case LITERAL:
                return new Expression<?>[] { cb.literal(values[0]), cb.literal(values[1]) };
            case LITERAL_BIGDECIMAL:
                return new Expression<?>[] { cb.literal(new BigDecimal(values[0])), cb.literal(new BigDecimal(values[1])) };
            case LITERAL_DOUBLE:
                return new Expression<?>[] { cb.literal(Double.valueOf(values[0])), cb.literal(Double.valueOf(values[1])) };
            case LITERAL_FLOAT:
                return new Expression<?>[] { cb.literal(Float.valueOf(values[0])), cb.literal(Float.valueOf(values[1])) };
            case LITERAL_SHORT:
                return new Expression<?>[] { cb.literal(Short.valueOf(values[0])), cb.literal(Short.valueOf(values[1])) };
            case LITERAL_INTEGER:
                return new Expression<?>[] { cb.literal(Integer.valueOf(values[0])), cb.literal(Integer.valueOf(values[1])) };
            case LITERAL_LONG:
                return new Expression<?>[] { cb.literal(Long.valueOf(values[0])), cb.literal(Long.valueOf(values[1])) };
            case LITERAL_COLUMN:
                return new Expression<?>[] { cb.literal(values[0]), cb.literal(values[1]) };
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
    private Expression<?>[] fieldValueConverts(AnnotationDescriptor<Between> ad, Object obj, Collection<?> fieldValue, Root<?> root, AbstractQuery<?> query,
        CriteriaBuilder cb) {
        List<?> fieldValues = List.class.isInstance(fieldValue) ? (List<?>) fieldValue : new ArrayList<>(fieldValue);
        switch (ad.getValueType()) {
            case AUTO:
                return new Expression<?>[] { cb.literal(fieldValues.get(0)), cb.literal(fieldValues.get(1)) };
            case LITERAL:
            case LITERAL_BIGDECIMAL:
            case LITERAL_DOUBLE:
            case LITERAL_FLOAT:
            case LITERAL_SHORT:
            case LITERAL_INTEGER:
            case LITERAL_LONG:
            case LITERAL_COLUMN:
                return this.handleLiteralValueTypes(ad, cb);
            case VALUE:
                return new Expression<?>[] { cb.literal(fieldValues.get(0)), cb.literal(fieldValues.get(1)) };
            case COLUMN:
                return new Expression<?>[] { root.get(Objects.toString(fieldValues.get(0))), root.get(Objects.toString(fieldValues.get(1))) };
            case FUNCTION:
                Functions functions = ad.getValueFunctions();
                return new Expression<?>[] {
                    cb.function(functions.value()[0].value(), functions.value()[0].type(),
                        this.createFunctionExpression(ad, functions.value()[0], obj, fieldValues, root, query, cb)),
                    cb.function(functions.value()[1].value(), functions.value()[1].type(),
                        this.createFunctionExpression(ad, functions.value()[1], obj, fieldValues, root, query, cb)) };
            case UDFUNCTION:
                try {
                    return this.applyBiValueProcessor(ad, obj, fieldValues, root, query, cb);
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
            switch (this.type) {
                case NOT_BETWEEN:
                    return cb.between(leftExpression.get(), rightExpression.get()[0], rightExpression.get()[1]).not();
                default:
                    return cb.between(leftExpression.get(), rightExpression.get()[0], rightExpression.get()[1]);
            }
        }
        return null;
    }

}
