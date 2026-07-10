package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.JpaExpressionResolver;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.JpaPathCompiler;

/**
 * tuple-value 谓词执行器.
 *
 * <p>
 * 该类型统一承接 {@code TupleInValues / TupleNotInValues} 的运行时执行：
 * 解析触发器、读取 tuple 数据源、归一化行、提取列值、做类型转换, 再将每一行展开为
 * {@code and}, 最终用 {@code or} 聚合.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledTuplePredicateSupport {

    private CompiledTuplePredicateSupport() {
    }

    public static Predicate create(final CompiledAnnotationSpec<? extends Annotation> ownerSpec, final Object args, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        CompiledTuplePredicateSpec tupleSpec = CompiledTupleValueSpecs.tuplePredicate(ownerSpec);
        Object ownerFieldValue = ownerSpec.readAndTrim(args);
        Predicate nullPredicate = CompiledSubquerySupport.requiredNullPredicate(ownerSpec, ownerFieldValue, root);
        if (Objects.nonNull(nullPredicate)) {
            return nullPredicate;
        }
        if (!TupleSourceResolver.isEnabled(tupleSpec, ownerFieldValue) || ownerSpec.shouldIgnore(ownerFieldValue) && !tupleSpec.usesHostFieldAsTupleSource()) {
            return null;
        }
        Object tupleSource = TupleSourceResolver.resolveTupleSource(tupleSpec, args, ownerFieldValue);
        if (tupleSpec.usesHostFieldAsTupleSource() && ownerSpec.shouldIgnore(tupleSource)) {
            return null;
        }
        List<TupleRowNormalizer.TupleRowValue> rows = TupleRowNormalizer.normalize(tupleSource);
        if (rows.isEmpty()) {
            return null;
        }
        List<Predicate> rowPredicates = new ArrayList<Predicate>();
        int rowIndex = 0;
        for (TupleRowNormalizer.TupleRowValue row : rows) {
            Predicate rowPredicate = rowPredicate(tupleSpec, row, rowIndex++, root, cb);
            if (Objects.nonNull(rowPredicate)) {
                rowPredicates.add(rowPredicate);
            }
        }
        if (rowPredicates.isEmpty()) {
            return null;
        }
        Predicate predicate = 1 == rowPredicates.size() ? rowPredicates.get(0) : cb.or(rowPredicates.toArray(new Predicate[rowPredicates.size()]));
        return applyNegate(tupleSpec, ownerSpec, predicate);
    }

    private static Predicate rowPredicate(final CompiledTuplePredicateSpec tupleSpec, final TupleRowNormalizer.TupleRowValue row, final int rowIndex,
        final Root<?> root, final CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        int columnIndex = 0;
        for (CompiledTupleColumnSpec column : tupleSpec.getColumns()) {
            Object rawValue = TupleValueAccessor.read(row, column);
            if (Objects.isNull(rawValue)) {
                return null;
            }
            Path<?> left = JpaPathCompiler.compile(root, column.getLeftPath());
            Object convertedValue = convertValue(tupleSpec, rowIndex, columnIndex, column, rawValue, left);
            predicates.add(cb.equal(left, literal(cb, convertedValue, left)));
            columnIndex++;
        }
        if (predicates.isEmpty()) {
            return null;
        }
        return 1 == predicates.size() ? predicates.get(0) : cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    private static Object convertValue(final CompiledTuplePredicateSpec tupleSpec, final int rowIndex, final int columnIndex,
        final CompiledTupleColumnSpec column, final Object rawValue, final Path<?> left) {
        Class<?> targetType = column.hasExplicitTargetType() ? column.getTargetType() : defaultTargetType(left, rawValue);
        try {
            return TupleValueConverter.convert(rawValue, targetType, column.getTargetFormat());
        } catch (RuntimeException ex) {
            throw conversionError(tupleSpec, rowIndex, columnIndex, column, rawValue, targetType, ex);
        }
    }

    private static Class<?> defaultTargetType(final Path<?> left, final Object rawValue) {
        if (Objects.nonNull(left) && Objects.nonNull(left.getJavaType()) && Object.class != left.getJavaType()) {
            return left.getJavaType();
        }
        return Objects.nonNull(rawValue) ? rawValue.getClass() : Object.class;
    }

    private static IllegalArgumentException conversionError(final CompiledTuplePredicateSpec tupleSpec, final int rowIndex, final int columnIndex,
        final CompiledTupleColumnSpec column, final Object rawValue, final Class<?> targetType, final RuntimeException cause) {
        StringBuilder builder = new StringBuilder();
        builder.append("Tuple value convert failed: field=").append(tupleSpec.getOwnerSpec().getValueFieldName());
        builder.append(", tupleField=").append(tupleSpec.getTupleField());
        builder.append(", row=").append(rowIndex);
        builder.append(", column=").append(columnIndex);
        builder.append(", leftPath=").append(column.getLeftPath());
        builder.append(", itemPath=").append(column.getItemPath());
        builder.append(", itemIndex=").append(column.getItemIndex());
        builder.append(", rawValue=").append(Objects.toString(rawValue));
        builder.append(", targetType=").append(null == targetType ? "null" : targetType.getName());
        return new IllegalArgumentException(builder.toString(), cause);
    }

    private static Predicate applyNegate(final CompiledTuplePredicateSpec tupleSpec, final CompiledAnnotationSpec<? extends Annotation> ownerSpec,
        final Predicate predicate) {
        boolean negate = tupleSpec.isNegate() ^ ownerSpec.getEffectiveOptions().isNegate();
        return negate ? predicate.not() : predicate;
    }

    @SuppressWarnings("unchecked")
    private static <T> Expression<T> literal(final CriteriaBuilder cb, final Object value, final Path<?> left) {
        return (Expression<T>) JpaExpressionResolver.literal(cb, value, left, "");
    }

}
