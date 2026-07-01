package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Cases 的结构化 then-group 执行器。
 *
 * <p>
 * 与通用 {@link CompiledPredicateGroupResolver} 不同，这里会把当前分支已经规范化过的字段值
 * 继续透传给每个叶子节点，确保 default value / trim / ignore 分支语义保持一致。
 * </p>
 */
public final class CompiledCasePredicateGroupResolver {

    private CompiledCasePredicateGroupResolver() {
    }

    public static Predicate create(final PredicateGroupSpec groupSpec, final Object args, final Object fieldValue, final Root<?> root,
        final AbstractQuery<?> query, final CriteriaBuilder cb) {
        if (Objects.isNull(groupSpec) || groupSpec.isEmpty()) {
            return null;
        }
        List<Predicate> predicates = new ArrayList<Predicate>();
        for (CompiledAnnotationSpec<?> annotation : groupSpec.getAnnotations()) {
            Predicate predicate = CompiledBuiltInPredicateSupport.createCasePredicate(annotation, args, fieldValue, root, query, cb);
            if (Objects.nonNull(predicate)) {
                predicates.add(predicate);
            }
        }
        for (PredicateGroupSpec child : groupSpec.getGroups()) {
            Predicate predicate = create(child, args, fieldValue, root, query, cb);
            if (Objects.nonNull(predicate)) {
                predicates.add(predicate);
            }
        }
        if (predicates.isEmpty()) {
            return null;
        }
        Predicate[] predicateArray = predicates.toArray(new Predicate[predicates.size()]);
        return PredicateGroupSpec.JunctionType.OR == groupSpec.getJunctionType() ? cb.or(predicateArray) : cb.and(predicateArray);
    }

}
