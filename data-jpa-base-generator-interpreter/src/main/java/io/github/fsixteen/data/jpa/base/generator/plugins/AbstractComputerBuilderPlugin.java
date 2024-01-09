package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.ArgsProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.DefaultArgsProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.DefaultValueProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Args;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Function;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * 注解解释器抽像类.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public abstract class AbstractComputerBuilderPlugin<A extends Annotation> implements BuilderPlugin<A> {
    static final Map<Object, ArgsProcessor> ARGS_FUN_PROCESSOR_CACHE = new ConcurrentHashMap<>(1 << 6);
    static Logger LOG = LoggerFactory.getLogger(AbstractComputerBuilderPlugin.class);

    /**
     * 校验函数参数处理器实现类.
     * 
     * @param arg 函数参数处理器执行信息.
     * @return boolean true: 可用, false: 不可用
     * @since 1.0.2
     */
    private boolean checkArgsFunctionProcessor(final Args arg) {
        // 无效配置
        if (arg.funClassName().isEmpty() && DefaultArgsProcessor.class == arg.funClass()) {
            return false;
        }
        // 可能有效配置
        try {
            // 首先尝试 funClassName
            final String className = arg.funClassName();
            if (!className.isEmpty()) {
                // funClassName 有效
                if (ARGS_FUN_PROCESSOR_CACHE.containsKey(className)) {
                    // 曾处理过
                    return true;
                }
                synchronized (ARGS_FUN_PROCESSOR_CACHE) {
                    if (ARGS_FUN_PROCESSOR_CACHE.containsKey(className)) {
                        return true;
                    }
                    Class<?> clazz = Class.forName(className);
                    ARGS_FUN_PROCESSOR_CACHE.put(className, (ArgsProcessor) clazz.getDeclaredConstructor().newInstance());
                }
                return true;
            }
        } catch (ReflectiveOperationException e) {
            LOG.error(e.getMessage(), e);
            // 反射失败, 继续执行, 尝试 processorClass
        }
        try {
            // 再次尝试 funClass
            Class<?> processorClass = arg.funClass();
            if (DefaultValueProcessor.class != processorClass) {
                // funClass 有效
                if (ARGS_FUN_PROCESSOR_CACHE.containsKey(processorClass)) {
                    // 曾处理过
                    return true;
                }
                synchronized (ARGS_FUN_PROCESSOR_CACHE) {
                    if (ARGS_FUN_PROCESSOR_CACHE.containsKey(processorClass)) {
                        return true;
                    }
                    ARGS_FUN_PROCESSOR_CACHE.put(processorClass, (ArgsProcessor) processorClass.getDeclaredConstructor().newInstance());
                }
                return true;
            }
        } catch (ReflectiveOperationException e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 获取函数参数处理器实现类.
     * 
     * @param arg 函数参数处理器执行信息.
     * @return {@linkplain io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.FieldProcessor
     *         FieldProcessor}
     * @since 1.0.2
     */
    private ArgsProcessor getArgsFunctionProcessor(final Args arg) {
        this.checkArgsFunctionProcessor(arg);
        return ARGS_FUN_PROCESSOR_CACHE.getOrDefault(arg.funClassName(), ARGS_FUN_PROCESSOR_CACHE.get(arg.funClass()));
    }

    /**
     * 创建函数参数单个表达式.
     * 
     * @param ad         注解描述信息实例.
     * @param arg        函数参数实例.
     * @param obj        原实例.
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return Expression
     */
    private Expression<?> creaateArgsExpression(final AnnotationDescriptor<A> ad, final Args arg, final Object obj, final Object fieldValue, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        switch (arg.type()) {
            case AUTO:
                return root.get(ad.getComputerFieldName());
            case LITERAL:
                return cb.literal(arg.value());
            case LITERAL_BIGDECIMAL:
                return cb.literal(new BigDecimal(arg.value()));
            case LITERAL_DOUBLE:
                return cb.literal(Double.parseDouble(arg.value()));
            case LITERAL_FLOAT:
                return cb.literal(Float.parseFloat(arg.value()));
            case LITERAL_SHORT:
                return cb.literal(Short.parseShort(arg.value()));
            case LITERAL_INTEGER:
                return cb.literal(Integer.parseInt(arg.value()));
            case LITERAL_LONG:
                return cb.literal(Long.parseLong(arg.value()));
            case VALUE:
                return cb.literal(fieldValue);
            case COLUMN:
                return root.get(arg.value());
            case FUNCTION:
                // TODO :: 规划中
                return null;
            case UDFUNCTION:
                return this.getArgsFunctionProcessor(arg).create(ad.getAnno(), arg, obj, ad.getComputerFieldName(), fieldValue, root, query, cb);
            default:
                return root.get(arg.value().isEmpty() ? ad.getComputerFieldName() : arg.value());
        }
    }

    /**
     * 创建函数参数表达式.
     * 
     * @param ad         注解描述信息实例.
     * @param function   函数实例.
     * @param obj        原实例.
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return Expression[]
     */
    protected Expression<?>[] createFunctionExpression(final AnnotationDescriptor<A> ad, final Function function, final Object obj, final Object fieldValue,
        final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) {
        List<Expression<?>> args = new ArrayList<>();
        for (Args arg : function.args()) {
            args.add(this.creaateArgsExpression(ad, arg, obj, fieldValue, root, query, cb));
        }
        return args.toArray(new Expression<?>[args.size()]);
    }

    /**
     * 逻辑忽略状态.
     * 
     * @param ad         注解描述信息实例.
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return boolean
     */
    public boolean checkField(final AnnotationDescriptor<A> ad, final Object fieldValue, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb) {
        switch (ad.getFieldType()) {
            case AUTO:
                return !"".equals(ad.getComputerFieldName());
            case LITERAL:
            case VALUE:
                return true;
            case LITERAL_BIGDECIMAL:
            case LITERAL_DOUBLE:
            case LITERAL_FLOAT:
            case LITERAL_SHORT:
            case LITERAL_INTEGER:
            case LITERAL_LONG:
                return !"".equals(ad.getFieldLiteral()) && Pattern.matches("^(\\-|\\+)?\\d+(\\.\\d+)?$", ad.getFieldLiteral());
            case COLUMN:
                return !"".equals(fieldValue);
            case FUNCTION:
                return Objects.nonNull(ad.getFieldFunction()) && !"".equals(ad.getFieldFunction().value());
            case UDFUNCTION:
                return this.checkFieldProcessor(ad.getFieldProcessor());
            default:
                return false;
        }
    }

    /**
     * 转换当前值.
     * 
     * @param <T>        表达式泛型.
     * @param ad         注解描述信息实例.
     * @param obj        原实例.
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    public <T> Expression<T> fieldConvert(final AnnotationDescriptor<A> ad, final Object obj, final Object fieldValue, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        switch (ad.getFieldType()) {
            case AUTO:
                return root.<T>get(ad.getComputerFieldName());
            case LITERAL:
                return cb.<T>literal((T) ad.getFieldLiteral());
            case LITERAL_BIGDECIMAL:
                return cb.<T>literal((T) new BigDecimal(ad.getFieldLiteral()));
            case LITERAL_DOUBLE:
                return cb.<T>literal((T) Double.valueOf(ad.getFieldLiteral()));
            case LITERAL_FLOAT:
                return cb.<T>literal((T) Float.valueOf(ad.getFieldLiteral()));
            case LITERAL_SHORT:
                return cb.<T>literal((T) Short.valueOf(ad.getFieldLiteral()));
            case LITERAL_INTEGER:
                return cb.<T>literal((T) Integer.valueOf(ad.getFieldLiteral()));
            case LITERAL_LONG:
                return cb.<T>literal((T) Long.valueOf(ad.getFieldLiteral()));
            case VALUE:
                return cb.<T>literal((T) fieldValue);
            case COLUMN:
                return root.<T>get(String.class.cast(fieldValue));
            case FUNCTION:
                Function function = ad.getFieldFunction();
                return cb.<T>function(function.value(), (Class<T>) function.type(),
                    this.createFunctionExpression(ad, function, obj, fieldValue, root, query, cb));
            case UDFUNCTION:
                try {
                    return this.<T>applyFieldProcessor(ad, obj, fieldValue, root, query, cb);
                } catch (ReflectiveOperationException e) {
                    LOG.error(e.getMessage(), e);
                    return null;
                }
            default:
                return null;
        }
    }

    /**
     * 校验当前值.<br>
     * 
     * <pre>
     * 历史逻辑: 数据判断数据类型.
     * case VALUE:
     *      // 实体中参数计算字段的数据类型.
     *      Class<?> javaType = root.getModel().getAttribute(ad.getComputerFieldName()).getJavaType();
     *      // 判断参与计算的值数据类型与目标字段数据类型一致性
     *      return javaType.isInstance(fieldValue) && javaType == ad.getValueField().getType();
     *
     * 新逻辑: 不在判断数据类型.
     * case VALUE:
     *     return true;
     * </pre>
     * 
     * @param ad         注解描述信息实例.
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return boolean
     */
    public boolean checkFieldValue(final AnnotationDescriptor<A> ad, final Object fieldValue, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb) {
        switch (ad.getValueType()) {
            case AUTO:
                return !"".equals(ad.getComputerFieldName());
            case LITERAL:
            case VALUE:
                return true;
            case LITERAL_BIGDECIMAL:
            case LITERAL_DOUBLE:
            case LITERAL_FLOAT:
            case LITERAL_SHORT:
            case LITERAL_INTEGER:
            case LITERAL_LONG:
                return !"".equals(ad.getValueLiteral()) && Pattern.matches("^(\\-|\\+)?\\d+(\\.\\d+)?$", ad.getValueLiteral());
            case COLUMN:
                return !"".equals(fieldValue);
            case FUNCTION:
                return Objects.nonNull(ad.getValueFunction()) && !"".equals(ad.getValueFunction().value());
            case UDFUNCTION:
                return this.checkValueProcessor(ad.getValueProcessor());
            default:
                return false;
        }
    }

    /**
     * 转换当前值.
     * 
     * @param <T>        表达式泛型.
     * @param ad         注解描述信息实例.
     * @param obj        原实例.
     * @param fieldValue 当前值.
     * @param root       见{@link javax.persistence.criteria.Root}.
     * @param query      见{@link javax.persistence.criteria.AbstractQuery}.
     * @param cb         见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    public <T> Expression<T> fieldValueConvert(final AnnotationDescriptor<A> ad, final Object obj, final Object fieldValue, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        switch (ad.getValueType()) {
            case AUTO:
                return root.<T>get(ad.getComputerFieldName());
            case LITERAL:
                return cb.<T>literal((T) ad.getValueLiteral());
            case LITERAL_BIGDECIMAL:
                return cb.<T>literal((T) new BigDecimal(ad.getValueLiteral()));
            case LITERAL_DOUBLE:
                return cb.<T>literal((T) Double.valueOf(ad.getValueLiteral()));
            case LITERAL_FLOAT:
                return cb.<T>literal((T) Float.valueOf(ad.getValueLiteral()));
            case LITERAL_SHORT:
                return cb.<T>literal((T) Short.valueOf(ad.getValueLiteral()));
            case LITERAL_INTEGER:
                return cb.<T>literal((T) Integer.valueOf(ad.getValueLiteral()));
            case LITERAL_LONG:
                return cb.<T>literal((T) Long.valueOf(ad.getValueLiteral()));
            case VALUE:
                return cb.<T>literal((T) fieldValue);
            case COLUMN:
                return root.<T>get(String.class.cast(fieldValue));
            case FUNCTION:
                Function function = ad.getValueFunction();
                return cb.<T>function(function.value(), (Class<T>) function.type(),
                    this.createFunctionExpression(ad, function, obj, fieldValue, root, query, cb));
            case UDFUNCTION:
                try {
                    return this.<T>applyValueProcessor(ad, obj, fieldValue, root, query, cb);
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
    public ComputerDescriptor<A> toPredicate(AnnotationDescriptor<A> ad, Object obj, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb)
        throws ClassNotFoundException {
        try {
            Object fieldValue = this.getFieldValue(ad, obj);

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
     * 创建 {@link javax.persistence.criteria.Predicate} 谓词.
     * 
     * @param cb              见{@link javax.persistence.criteria.CriteriaBuilder}.
     * @param leftExpression  表达式1
     * @param rightExpression 表达式2
     * @return Predicate
     */
    @SuppressWarnings("rawtypes")
    protected Predicate createPredicate(CriteriaBuilder cb, Optional<Expression> leftExpression, Optional<Expression> rightExpression) {
        return null;
    }

}
