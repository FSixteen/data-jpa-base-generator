package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Null;
import io.github.fsixteen.data.jpa.base.generator.plugins.codecs.LiteralCodecs;
import io.github.fsixteen.data.jpa.base.generator.plugins.codecs.TargetTypeConverters;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.JpaExpressionResolver;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.JpaPathCompiler;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.RuntimeValueResolver;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.ExpressionCardinality;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.ExpressionSource;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FieldValueExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.LiteralExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PredicateExpression;

/**
 * compiled 主链路下的内建谓词支持。
 *
 * <p>
 * 内建 provider 统一先消费 {@link CompiledPredicateSpec}，再将表达式规格落成 JPA 谓词。
 * 这样现有快捷注解与后续 canonical 注解都能共享同一套执行规则。
 * </p>
 *
 * <p>
 * 这里集中处理 compare、between、in、null-check 这几类最基础的谓词落地逻辑，
 * 并统一复用路径编译、运行时取值、literal codec 与目标类型转换能力。
 * 只要上游已经把注解编译成标准表达式规格，执行阶段就不需要再感知原始注解的历史字段差异。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledBuiltInPredicateSupport {

    private CompiledBuiltInPredicateSupport() {
    }

    /**
     * 为 Cases 分支场景执行一条已补齐的 canonical compare 谓词。
     */
    public static Predicate createCasePredicate(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Object fieldValue,
        final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) {
        ComparableType type = CompiledComparableTypeResolver.resolve(spec.getPredicateCore().getOp(), spec.getCollectionPolicy().isSplit());
        if (type.isRange()) {
            CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.between(type.getOperator(), spec);
            Collection<?> betweenValue = transitionBetweenValue(fieldValue);
            Optional<Expression<?>> leftExpression = resolveSingle(spec, predicateSpec.getLeft(), args, fieldValue, root, query, cb);
            Optional<RangeExpressions> rightExpression = resolveRange(spec, predicateSpec, args, betweenValue, root, query, cb);
            if (!leftExpression.isPresent() || !rightExpression.isPresent()) {
                return null;
            }
            Predicate predicate = betweenPredicate(cb, leftExpression.get(), rightExpression.get());
            return ComparableType.NOT_BETWEEN == type ? predicate.not() : predicate;
        }
        if (type.isCollection()) {
            Collection<?> collectionValue = transitionInValue(spec, fieldValue);
            if (Objects.isNull(collectionValue)) {
                return null;
            }
            List<?> normalizedValues = collectionValue.stream().filter(filterBySpec(spec)).collect(Collectors.toList());
            if (normalizedValues.isEmpty()) {
                return null;
            }
            CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.in(type.getOperator(), spec);
            Optional<Expression<?>> leftExpression = resolveSingle(spec, predicateSpec.getLeft(), args, normalizedValues, root, query, cb);
            Optional<Expression<?>> rightExpression = resolveCollection(spec, predicateSpec.getRight(), args, normalizedValues, root, query, cb);
            if (!leftExpression.isPresent() || !rightExpression.isPresent()) {
                return null;
            }
            Predicate predicate = leftExpression.get().in(rightExpression.get());
            return type.isNegated() ? predicate.not() : predicate;
        }
        if (type.isNullCheck()) {
            Optional<Expression<?>> leftExpression = resolveSingle(spec, CompiledPredicateSpecs.nullCheck(type.getOperator(), spec).getLeft(), args, fieldValue,
                root, query, cb);
            if (!leftExpression.isPresent()) {
                return null;
            }
            return ComparableType.IS_NOT_NULL == type ? leftExpression.get().isNotNull() : leftExpression.get().isNull();
        }
        return createResolvedBinaryPredicate(spec, args, fieldValue, root, query, cb, type.getOperator(), false, true);
    }

    /**
     * 执行普通 compare 谓词。
     */
    public static Predicate createComparable(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb, final ComparableType type) {
        Object fieldValue = readFieldValue(spec, args);
        Predicate nullPredicate = requiredNullPredicate(spec, fieldValue, root);
        if (null != nullPredicate || spec.shouldIgnore(fieldValue)) {
            return nullPredicate;
        }
        return createResolvedBinaryPredicate(spec, args, fieldValue, root, query, cb, type.getOperator(), true, false);
    }

    /**
     * 执行 like 家族谓词。
     */
    public static Predicate createLike(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb, final ComparableType type) {
        Object fieldValue = readFieldValue(spec, args);
        Predicate nullPredicate = requiredNullPredicate(spec, fieldValue, root);
        if (null != nullPredicate || spec.shouldIgnore(fieldValue)) {
            return nullPredicate;
        }
        return createResolvedBinaryPredicate(spec, args, fieldValue, root, query, cb, type.getOperator(), true, false);
    }

    /**
     * 执行 between / not-between 谓词。
     */
    public static Predicate createBetween(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb, final ComparableType type) {
        Object rawFieldValue = readFieldValue(spec, args);
        Collection<?> fieldValue = transitionBetweenValue(rawFieldValue);
        Predicate nullPredicate = requiredNullPredicate(spec, fieldValue, root);
        if (null != nullPredicate || spec.shouldIgnore(fieldValue)) {
            return nullPredicate;
        }
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.between(type.getOperator(), spec);
        Optional<Expression<?>> leftExpression = resolveSingle(spec, predicateSpec.getLeft(), args, fieldValue, root, query, cb);
        Optional<RangeExpressions> rightExpression = resolveRange(spec, predicateSpec, args, fieldValue, root, query, cb);
        if (!leftExpression.isPresent() || !rightExpression.isPresent()) {
            return null;
        }
        Predicate predicate = betweenPredicate(cb, leftExpression.get(), rightExpression.get());
        if (ComparableType.NOT_BETWEEN == type) {
            predicate = predicate.not();
        }
        return reverseIfRequired(spec, predicate);
    }

    /**
     * 执行 in / not-in 谓词。
     */
    public static Predicate createIn(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb, final ComparableType type) {
        Collection<?> fieldValue = transitionInValue(spec, readFieldValue(spec, args));
        Predicate nullPredicate = requiredNullPredicate(spec, fieldValue, root);
        if (null != nullPredicate) {
            return nullPredicate;
        }
        if (Objects.isNull(fieldValue)) {
            return null;
        }
        List<?> normalizedValues = fieldValue.stream().filter(it -> !spec.shouldIgnore(it)).filter(filterBySpec(spec)).collect(Collectors.toList());
        if (normalizedValues.isEmpty() || spec.shouldIgnore(normalizedValues)) {
            return null;
        }
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.in(type.getOperator(), spec);
        Optional<Expression<?>> leftExpression = resolveSingle(spec, predicateSpec.getLeft(), args, normalizedValues, root, query, cb);
        Optional<Expression<?>> rightExpression = resolveCollection(spec, predicateSpec.getRight(), args, normalizedValues, root, query, cb);
        if (!leftExpression.isPresent() || !rightExpression.isPresent()) {
            return null;
        }
        Predicate predicate = leftExpression.get().in(rightExpression.get());
        if (type.isNegated()) {
            predicate = predicate.not();
        }
        return reverseIfRequired(spec, predicate);
    }

    /**
     * 执行布尔开关式 null 谓词。
     */
    public static Predicate createNull(final CompiledAnnotationSpec<Null> spec, final Object args, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb) {
        Object fieldValue = readFieldValue(spec, args);
        Predicate nullPredicate = requiredNullPredicate(spec, fieldValue, root);
        if (null != nullPredicate || !(fieldValue instanceof Boolean) || spec.shouldIgnore(fieldValue)) {
            return nullPredicate;
        }
        Optional<Expression<?>> leftExpression = resolveSingle(spec, CompiledPredicateSpecs.nullCheck(PredicateOperator.NULL_SWITCH, spec).getLeft(), args,
            fieldValue, root, query, cb);
        if (!leftExpression.isPresent()) {
            return null;
        }
        Predicate predicate = Objects.equals(Boolean.TRUE, fieldValue) ? spec.getAnnotation().whenTrueUse().apply(leftExpression.get())
            : spec.getAnnotation().whenFalseUse().apply(leftExpression.get());
        return reverseIfRequired(spec, predicate);
    }

    /**
     * 执行 is-null / is-not-null 谓词。
     */
    public static Predicate createIsNull(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb, final boolean notNull) {
        Object fieldValue = readFieldValue(spec, args);
        Predicate nullPredicate = requiredNullPredicate(spec, fieldValue, root);
        if (null != nullPredicate) {
            return nullPredicate;
        }
        if (!Objects.equals(Boolean.TRUE, fieldValue) || spec.shouldIgnore(fieldValue)) {
            return null;
        }
        Optional<Expression<?>> leftExpression = resolveSingle(spec,
            CompiledPredicateSpecs.nullCheck(notNull ? PredicateOperator.IS_NOT_NULL : PredicateOperator.IS_NULL, spec).getLeft(), args, fieldValue, root,
            query, cb);
        if (!leftExpression.isPresent()) {
            return null;
        }
        Predicate predicate = notNull ? leftExpression.get().isNotNull() : leftExpression.get().isNull();
        return reverseIfRequired(spec, predicate);
    }

    /**
     * 将单值 canonical 表达式解析为 JPA {@link Expression}。
     */
    private static Optional<Expression<?>> resolveSingle(final CompiledAnnotationSpec<? extends Annotation> spec, final PredicateExpression expression,
        final Object args, final Object fieldValue, final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) {
        if (Objects.isNull(expression)) {
            return Optional.empty();
        }
        if (!JpaExpressionResolver.canResolve(expression, args, fieldValue)) {
            return Optional.empty();
        }
        return Optional.ofNullable(JpaExpressionResolver.resolve(expression, args, fieldValue, root, query, cb));
    }

    /**
     * 将集合语义表达式解析为 JPA {@link Expression}。
     */
    private static Optional<Expression<?>> resolveCollection(final CompiledAnnotationSpec<? extends Annotation> spec, final PredicateExpression expression,
        final Object args, final List<?> fieldValue, final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) {
        if (Objects.isNull(expression)) {
            return Optional.empty();
        }
        if (expression instanceof FieldValueExpression && ExpressionCardinality.COLLECTION == expression.getCardinality()) {
            Object runtimeValue = RuntimeValueResolver.resolve((FieldValueExpression) expression, args, fieldValue);
            if (ExpressionSource.FIELD_VALUE == expression.getSource()) {
                return Optional.of(cb.literal(runtimeValue));
            }
            if (ExpressionSource.FIELD_VALUE_PATH == expression.getSource()) {
                Collection<?> runtimeCollection = transitionCollectionValue(runtimeValue);
                Object pathValue = 1 == runtimeCollection.size() ? runtimeCollection.iterator().next() : runtimeCollection;
                return Optional.of(JpaPathCompiler.compile(root, Objects.toString(pathValue)));
            }
        }
        if (expression instanceof LiteralExpression && ExpressionCardinality.COLLECTION == expression.getCardinality()) {
            LiteralExpression literalExpression = (LiteralExpression) expression;
            return Optional.of(cb.literal(
                LiteralCodecs.parseCollection(literalExpression.getRawValue(), spec.getCollectionPolicy().getDecollator(), literalExpression.getJavaType())));
        }
        return resolveSingle(spec, expression, args, fieldValue, root, query, cb);
    }

    /**
     * 解析 between 比较所需的起止表达式。
     */
    private static Optional<RangeExpressions> resolveRange(final CompiledAnnotationSpec<? extends Annotation> spec, final CompiledPredicateSpec predicateSpec,
        final Object args, final Collection<?> fieldValue, final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) {
        PredicateExpression right = predicateSpec.getRight();
        if (Objects.isNull(right)) {
            return Optional.empty();
        }
        if (right instanceof FieldValueExpression && ExpressionCardinality.RANGE == right.getCardinality()) {
            Object runtimeValue = RuntimeValueResolver.resolve((FieldValueExpression) right, args, fieldValue);
            Collection<?> rangeValue = transitionBetweenValue(runtimeValue);
            if (Objects.isNull(rangeValue)) {
                return Optional.empty();
            }
            List<?> values = rangeValue instanceof List<?> ? (List<?>) rangeValue : new ArrayList<Object>(rangeValue);
            if (ExpressionSource.FIELD_VALUE == right.getSource()) {
                return Optional.of(RangeExpressions.of(cb.literal(values.get(0)), cb.literal(values.get(1))));
            }
            if (ExpressionSource.FIELD_VALUE_PATH == right.getSource()) {
                return Optional.of(RangeExpressions.of(JpaPathCompiler.compile(root, Objects.toString(values.get(0))),
                    JpaPathCompiler.compile(root, Objects.toString(values.get(1)))));
            }
        }
        if (right instanceof LiteralExpression && ExpressionCardinality.RANGE == right.getCardinality()) {
            LiteralExpression literalExpression = (LiteralExpression) right;
            List<Object> rangeValues = LiteralCodecs.parseRange(literalExpression.getRawValue(), Constant.DECOLLATOR, literalExpression.getJavaType());
            return Optional.of(RangeExpressions.of(cb.literal(rangeValues.get(0)), cb.literal(rangeValues.get(1))));
        }
        if (!predicateSpec.getExtraOperands().isEmpty()) {
            Optional<Expression<?>> start = resolveSingle(spec, right, args, fieldValue, root, query, cb);
            Optional<Expression<?>> end = resolveSingle(spec, predicateSpec.getExtraOperands().get(0), args, fieldValue, root, query, cb);
            if (start.isPresent() && end.isPresent()) {
                return Optional.of(RangeExpressions.of(start.get(), end.get()));
            }
        }
        return Optional.empty();
    }

    /**
     * 将运行时值规范为 between 语义需要的二元集合。
     */
    private static Collection<?> transitionBetweenValue(final Object fieldValue) {
        if (Objects.isNull(fieldValue)) {
            return null;
        }
        Collection<?> collection = asCollection(fieldValue);
        if (null != collection && 2 == collection.size()) {
            return collection;
        }
        Object[] array = asObjectArray(fieldValue);
        if (null != array && 2 == array.length) {
            return Arrays.asList(array);
        }
        return null;
    }

    /**
     * 将运行时值规范为 in 语义需要的集合。
     */
    private static Collection<?> transitionInValue(final CompiledAnnotationSpec<? extends Annotation> spec, final Object fieldValue) {
        if (Objects.isNull(fieldValue)) {
            return null;
        }
        if (fieldValue instanceof String && isSplitAnnotation(spec)) {
            // collection policy 的字符串拆分与 targetType 转换统一在 compiled 主链路完成。
            return TargetTypeConverters.parseCollection(String.class.cast(fieldValue), spec.getCollectionPolicy().getDecollator(),
                spec.getCollectionPolicy().getTargetType(), spec.getCollectionPolicy().getTargetFormat());
        }
        Collection<?> collection = asCollection(fieldValue);
        if (null != collection) {
            return collection;
        }
        Object[] array = asObjectArray(fieldValue);
        if (null != array) {
            return Arrays.asList(array);
        }
        return Arrays.asList(fieldValue);
    }

    /**
     * 将任意值尽可能归一为集合形式。
     */
    private static Collection<?> transitionCollectionValue(final Object fieldValue) {
        if (Objects.isNull(fieldValue)) {
            return null;
        }
        Collection<?> collection = asCollection(fieldValue);
        if (null != collection) {
            return collection;
        }
        Object[] array = asObjectArray(fieldValue);
        if (null != array) {
            return Arrays.asList(array);
        }
        return Arrays.asList(fieldValue);
    }

    private static boolean isSplitAnnotation(final CompiledAnnotationSpec<? extends Annotation> spec) {
        return spec.getCollectionPolicy().isSplit();
    }

    /**
     * 解析当前规格声明的集合元素过滤规则。
     */
    private static java.util.function.Predicate<Object> filterBySpec(final CompiledAnnotationSpec<? extends Annotation> spec) {
        CompiledCollectionPolicySpec collectionPolicy = spec.getCollectionPolicy();
        String regexp = collectionPolicy.getRegexp();
        if (null != regexp && !regexp.isEmpty()) {
            return (e) -> null != e && Pattern.matches(regexp, e.toString());
        }
        CompiledPredicateFilterSpec predicateFilter = collectionPolicy.getPredicateFilter();
        if (null != predicateFilter) {
            java.util.function.Predicate<Object> resolved = predicateFilter.resolve();
            if (null != resolved) {
                return resolved;
            }
        }
        return (e) -> true;
    }

    /**
     * 读取并按 trim 规则规范化绑定字段值。
     */
    private static Object readFieldValue(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args) {
        return spec.readAndTrim(args);
    }

    /**
     * 解析左右表达式后执行一条标准二元比较谓词。
     */
    private static Predicate createResolvedBinaryPredicate(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Object fieldValue,
        final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb, final PredicateOperator operator, final boolean reverse,
        final boolean resolveNullAsEqualityCheck) {
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.simple(operator, spec);
        Optional<BinaryExpressions> expressions = resolveBinaryExpressions(spec, predicateSpec, args, fieldValue, root, query, cb);
        if (!expressions.isPresent()) {
            return null;
        }
        if (resolveNullAsEqualityCheck && Objects.isNull(fieldValue)) {
            if (PredicateOperator.EQ == predicateSpec.getOperator()) {
                return expressions.get().left.isNull();
            }
            if (PredicateOperator.NEQ == predicateSpec.getOperator()) {
                return expressions.get().left.isNotNull();
            }
        }
        Predicate predicate = PredicateOperatorSupport.createBinary(predicateSpec.getOperator(), expressions.get().left, expressions.get().right, cb);
        return reverse ? reverseIfRequired(spec, predicate) : predicate;
    }

    /**
     * 同时解析二元比较的左右表达式。
     */
    private static Optional<BinaryExpressions> resolveBinaryExpressions(final CompiledAnnotationSpec<? extends Annotation> spec,
        final CompiledPredicateSpec predicateSpec, final Object args, final Object fieldValue, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb) {
        Optional<Expression<?>> leftExpression = resolveSingle(spec, predicateSpec.getLeft(), args, fieldValue, root, query, cb);
        Optional<Expression<?>> rightExpression = resolveSingle(spec, predicateSpec.getRight(), args, fieldValue, root, query, cb);
        if (!leftExpression.isPresent() || !rightExpression.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(new BinaryExpressions(leftExpression.get(), rightExpression.get()));
    }

    /**
     * 在 required 且字段值为 null 时生成短路谓词。
     */
    private static Predicate requiredNullPredicate(final CompiledAnnotationSpec<? extends Annotation> spec, final Object fieldValue, final Root<?> root) {
        return spec.getEffectiveOptions().isRequired() && Objects.isNull(fieldValue) ? root.get(spec.getBindingPath()).isNull() : null;
    }

    /**
     * 如公共选项声明了 not，则对当前谓词做一次取反。
     */
    private static Predicate reverseIfRequired(final CompiledAnnotationSpec<? extends Annotation> spec, final Predicate predicate) {
        return spec.getEffectiveOptions().isNegate() ? predicate.not() : predicate;
    }

    private static Predicate betweenPredicate(final CriteriaBuilder cb, final Expression<?> left, final RangeExpressions range) {
        return betweenComparable(cb, left, range.start, range.end);
    }

    @SuppressWarnings({ "unchecked" })
    private static <Y extends Comparable<? super Y>> Predicate betweenComparable(final CriteriaBuilder cb, final Expression<?> left, final Expression<?> start,
        final Expression<?> end) {
        return cb.between((Expression<? extends Y>) left, (Expression<? extends Y>) start, (Expression<? extends Y>) end);
    }

    private static Collection<?> asCollection(final Object fieldValue) {
        return fieldValue instanceof Collection<?> ? (Collection<?>) fieldValue : null;
    }

    private static Object[] asObjectArray(final Object fieldValue) {
        if (Objects.isNull(fieldValue) || !fieldValue.getClass().isArray()) {
            return null;
        }
        if (fieldValue instanceof Object[]) {
            return (Object[]) fieldValue;
        }
        int length = Array.getLength(fieldValue);
        Object[] normalized = new Object[length];
        for (int index = 0; index < length; index++) {
            normalized[index] = Array.get(fieldValue, index);
        }
        return normalized;
    }

    private static final class BinaryExpressions {

        private final Expression<?> left;

        private final Expression<?> right;

        private BinaryExpressions(final Expression<?> left, final Expression<?> right) {
            this.left = left;
            this.right = right;
        }

    }

    private static final class RangeExpressions {

        private final Expression<?> start;

        private final Expression<?> end;

        private static RangeExpressions of(final Expression<?> start, final Expression<?> end) {
            return new RangeExpressions(start, end);
        }

        private RangeExpressions(final Expression<?> start, final Expression<?> end) {
            this.start = start;
            this.end = end;
        }

    }

}
