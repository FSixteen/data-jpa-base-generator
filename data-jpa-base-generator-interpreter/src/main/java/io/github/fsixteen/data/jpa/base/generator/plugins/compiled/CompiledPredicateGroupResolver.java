package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProvider;
import io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProviderRegistry;

/**
 * compiled 谓词分组树执行器.
 *
 * <p>
 * 它把 {@link PredicateGroupSpec} 结构真正落地成 JPA {@link Predicate},
 * 供子查询、复杂对象和未来 exists/not exists 复用, 而不再各自拼装一套分组逻辑.
 * </p>
 *
 * <p>
 * 该类只负责“执行分组树”：
 * 注解如何编译成分组树由 {@link CompiledPredicateAssembler} 与
 * {@link CompiledSubqueryGroupSpecs} 决定；
 * 这里根据 junction、子组与注解顺序将它们递归还原为最终 Criteria 谓词.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledPredicateGroupResolver {

    private CompiledPredicateGroupResolver() {
    }

    /**
     * 递归执行一棵 compiled 分组树并还原为最终 JPA 谓词.
     *
     * <p>
     * 该方法会先按字段声明顺序稳定当前层叶子注解的执行顺序, 再递归处理子组,
     * 最后依据分组的 junction 类型拼接为 `and/or` 谓词.
     * </p>
     *
     * @param groupSpec 待执行的分组规格
     * @param args      当前请求或查询对象
     * @param root      JPA 查询根节点
     * @param query     当前查询对象
     * @param cb        Criteria 构造器
     * @return 构建完成的分组谓词；若分组为空或所有节点都未产出谓词则返回 {@code null}
     */
    public static Predicate create(final PredicateGroupSpec groupSpec, final Object args, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb) {
        if (Objects.isNull(groupSpec) || groupSpec.isEmpty()) {
            return null;
        }
        List<Predicate> predicates = new ArrayList<Predicate>();
        for (CompiledAnnotationSpec<?> annotation : sortAnnotations(groupSpec.getAnnotations())) {
            CompiledPredicateProvider provider = CompiledPredicateProviderRegistry.require(annotation.getAnnotationType());
            Predicate predicate = provider.create(annotation, args, root, query, cb);
            if (Objects.nonNull(predicate)) {
                predicates.add(predicate);
            }
        }
        for (PredicateGroupSpec child : groupSpec.getGroups()) {
            Predicate predicate = create(child, args, root, query, cb);
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

    /**
     * 基于参数类字段声明顺序稳定注解执行顺序, 避免分组树执行结果受反射返回顺序影响.
     *
     * @param annotations 当前层叶子注解列表
     * @return 稳定排序后的列表；当输入为空或元素少于 2 个时直接返回原列表
     */
    private static List<CompiledAnnotationSpec<?>> sortAnnotations(final List<CompiledAnnotationSpec<?>> annotations) {
        if (Objects.isNull(annotations) || annotations.size() < 2) {
            return annotations;
        }
        List<CompiledAnnotationSpec<?>> sorted = new ArrayList<CompiledAnnotationSpec<?>>(annotations);
        Map<String, Integer> indexes = fieldIndexes(sorted.get(0).getObjClass());
        sorted.sort(Comparator.comparingInt(spec -> indexes.getOrDefault(spec.getValueFieldName(), Integer.MAX_VALUE)));
        return sorted;
    }

    /**
     * 建立字段声明顺序索引, 用于稳定叶子注解执行顺序.
     *
     * @param type 参数对象类型
     * @return 字段名到声明顺序的索引映射；当类型为空时返回空映射
     */
    private static Map<String, Integer> fieldIndexes(final Class<?> type) {
        Map<String, Integer> indexes = new HashMap<String, Integer>();
        if (Objects.isNull(type)) {
            return indexes;
        }
        java.lang.reflect.Field[] fields = type.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            indexes.put(fields[i].getName(), i);
        }
        return indexes;
    }

}
