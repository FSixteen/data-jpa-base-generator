package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SubqueryGroup;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleExists;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleNotExists;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TuplePair;
import io.github.fsixteen.data.jpa.base.generator.plugins.support.MetaAnnotationAttributes;

/**
 * 多列 tuple 子查询 compiled spec 构造入口。
 *
 * <p>
 * 该类型负责从注解层提取：
 * </p>
 * <ul>
 * <li>子查询实体</li>
 * <li>多列关联 pairs</li>
 * <li>默认附加 whereCompare</li>
 * <li>附加 where-group</li>
 * </ul>
 *
 * <p>
 * 运行时子查询创建与 JPA Predicate 拼装由 {@link CompiledTupleSubquerySupport} 负责。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledTupleSubquerySpecs {

    private CompiledTupleSubquerySpecs() {
    }

    public static CompiledTupleSubquerySpec tupleSubquery(final CompiledAnnotationSpec<? extends Annotation> ownerSpec) {
        Annotation annotation = ownerSpec.getAnnotation();
        Class<?> targetEntity = targetEntity(annotation);
        boolean negate = annotation instanceof TupleNotExists;
        TuplePair[] rawPairs = pairs(annotation);
        if (null == rawPairs || 0 == rawPairs.length) {
            throw new IllegalArgumentException(ownerSpec.getAnnotationType().getSimpleName() + " requires non-empty pairs()");
        }
        List<CompiledTupleCorrelationSpec> pairs = new ArrayList<CompiledTupleCorrelationSpec>(rawPairs.length);
        String owner = "@" + ownerSpec.getAnnotationType().getSimpleName() + "(" + ownerSpec.getValueFieldName() + ")";
        for (TuplePair pair : rawPairs) {
            CompiledTupleCorrelationSpec spec = CompiledTupleCorrelationSpec.of(configuredPath(pair.left(), ownerSpec.getBindingPath()),
                configuredPath(pair.right(), ownerSpec.getBindingPath()));
            spec.validate(owner);
            pairs.add(spec);
        }
        PredicateGroupSpec groupSpec = CompiledSubqueryGroupSpecs.compile(ownerSpec, where(annotation));
        CompiledAnnotationSpec<?> nestedSpec = nestedWhereCompare(ownerSpec, whereCompare(annotation));
        return CompiledTupleSubquerySpec.of(ownerSpec, targetEntity, negate, pairs, nestedSpec, groupSpec);
    }

    private static CompiledAnnotationSpec<?> nestedWhereCompare(final CompiledAnnotationSpec<? extends Annotation> ownerSpec, final Compare whereCompare) {
        if (null != whereCompare && CompiledCanonicalCompareFactory.hasConfiguredCompare(whereCompare)) {
            return CompiledCanonicalCompareFactory.compareSpec(ownerSpec.getObjClass(), ownerSpec.getValueField(), whereCompare, ownerSpec.getBindingPath(),
                ownerSpec.getBindingPath());
        }
        return null;
    }

    private static String configuredPath(final Expr expr, final String fallback) {
        return CompiledCanonicalCompareFactory.configuredPath(expr, fallback);
    }

    private static Class<?> targetEntity(final Annotation annotation) {
        if (annotation instanceof TupleExists) {
            return TupleExists.class.cast(annotation).targetEntity();
        }
        if (annotation instanceof TupleNotExists) {
            return TupleNotExists.class.cast(annotation).targetEntity();
        }
        return MetaAnnotationAttributes.fieldValue(annotation, "targetEntity", Class.class);
    }

    private static TuplePair[] pairs(final Annotation annotation) {
        if (annotation instanceof TupleExists) {
            return TupleExists.class.cast(annotation).pairs();
        }
        if (annotation instanceof TupleNotExists) {
            return TupleNotExists.class.cast(annotation).pairs();
        }
        TuplePair[] pairs = MetaAnnotationAttributes.fieldValue(annotation, "pairs", TuplePair[].class);
        return Objects.isNull(pairs) ? new TuplePair[0] : pairs;
    }

    private static Compare whereCompare(final Annotation annotation) {
        return MetaAnnotationAttributes.fieldValue(annotation, "whereCompare", Compare.class);
    }

    private static SubqueryGroup where(final Annotation annotation) {
        return MetaAnnotationAttributes.fieldValue(annotation, "where", SubqueryGroup.class);
    }

}
