package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Cases 的结构化 then-group 执行器.
 *
 * <p>
 * 与通用 {@link CompiledPredicateGroupResolver} 不同, 这里会把当前分支已经规范化过的字段值
 * 继续透传给每个叶子节点, 确保 default value / trim / ignore 分支语义保持一致.
 * </p>
 */
public final class CompiledCasePredicateGroupResolver {

    private CompiledCasePredicateGroupResolver() {
    }

    /**
     * 将 `Cases` 分支中的结构化分组规格递归还原为 JPA {@link Predicate}.
     *
     * <p>
     * 与通用分组执行器相比, 这里会把当前分支已经过 default/trim/ignore 处理的字段值继续透传给叶子节点,
     * 以保证 then-group 中的 canonical compare 与分支本身使用完全一致的输入语义.
     * </p>
     *
     * @param groupSpec  待执行的结构化分组规格
     * @param args       当前请求或查询对象
     * @param fieldValue 当前分支上下文中的字段值
     * @param root       当前查询根节点
     * @param query      当前查询对象
     * @param cb         Criteria 构造器
     * @return 构建完成的分组谓词；若分组为空或全部叶子均无法生成谓词则返回 {@code null}
     */
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
