package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;
import io.github.fsixteen.data.jpa.base.generator.plugins.collections.AnnotationCollection;

/**
 * compiled 谓词分组与组装器.
 *
 * <p>
 * 该类型负责把一组已经生成好的 {@link CompiledPredicateResult} 按 scope 与 group 规则做归类、
 * 排序、递归合并, 并最终落成 {@link Predicate} 列表或 {@link PredicateGroupSpec} 分组树.
 * 它是当前 group 语义在运行期的唯一执行实现.
 * </p>
 *
 * <p>
 * 对外保留的 {@code AnnotationCollection -> ComputerCollection} 调用链最终也会经过这里,
 * 因此 scope 过滤、group 顺序与组间 AND/OR 规则都必须在该层保持稳定且可预测.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledPredicateAssembler {

    private final AnnotationCollection annotationCollection;

    private final List<CompiledPredicateResult<?>> results;

    private final CriteriaBuilder cb;

    private final String scope;

    private CompiledPredicateAssembler(final AnnotationCollection annotationCollection, final List<CompiledPredicateResult<?>> results,
        final CriteriaBuilder cb, final String scope) {
        this.annotationCollection = annotationCollection;
        this.results = results;
        this.cb = cb;
        this.scope = scope;
    }

    /**
     * 创建一份面向指定 scope 的 compiled 谓词组装器.
     */
    public static CompiledPredicateAssembler of(final AnnotationCollection annotationCollection, final List<CompiledPredicateResult<?>> results,
        final CriteriaBuilder cb, final String scope) {
        return new CompiledPredicateAssembler(annotationCollection, results, cb, scope);
    }

    /**
     * 先按注解声明的 group 名收集原子谓词.
     */
    private Map<String, List<Tuple<CompiledPredicateResult<?>>>> createStandardGroups() {
        // 先按注解声明的 group 粒度收集“原子谓词”, 后续再递归折叠成最终 DEFAULT 组.
        List<CompiledPredicateResult<?>> valid = this.results.stream().filter(it -> Objects.nonNull(it) && !it.isEmpty() && Objects.nonNull(it.getPredicate()))
            .filter(it -> containsScope(it.getSpec())).collect(Collectors.toList());
        Map<String, List<Tuple<CompiledPredicateResult<?>>>> standardGroups = new ConcurrentHashMap<String, List<Tuple<CompiledPredicateResult<?>>>>();
        for (CompiledPredicateResult<?> result : valid) {
            for (GroupInfo groupInfo : result.getSpec().getEffectiveOptions().getGroups()) {
                String conditionName = groupInfo.value();
                if (!standardGroups.containsKey(conditionName)) {
                    standardGroups.put(conditionName, new ArrayList<Tuple<CompiledPredicateResult<?>>>());
                }
                standardGroups.get(conditionName).add(Tuple.of(groupInfo.order(), result));
            }
        }
        return standardGroups;
    }

    /**
     * 按 group 规则将同组原子谓词折叠为上一级 group 的组合谓词.
     */
    private void mergerCompositeGroups(final Map<String, List<Tuple<Predicate>>> map, final String value, final List<Predicate> groupPs) {
        Predicate[] predicates = groupPs.toArray(new Predicate[groupPs.size()]);
        GroupComputerType mergeRule = groupRule(value);
        if (null == mergeRule) {
            if (!map.containsKey(Constant.DEFAULT)) {
                map.put(Constant.DEFAULT, new ArrayList<Tuple<Predicate>>());
            }
            map.get(Constant.DEFAULT).add(Tuple.of(0, this.cb.and(predicates)));
        } else {
            for (GroupInfo groupInfo : mergeRule.groups()) {
                String conditionName = groupInfo.value();
                if (!map.containsKey(conditionName)) {
                    map.put(conditionName, new ArrayList<Tuple<Predicate>>());
                }
                Predicate predicate = GroupJunctions.isOr(mergeRule) ? this.cb.or(predicates) : this.cb.and(predicates);
                map.get(conditionName).add(Tuple.of(groupInfo.order(), predicate));
            }
        }
    }

    /**
     * 将标准 group 聚合为第一层复合 group.
     */
    private Map<String, List<Tuple<Predicate>>> createCompositeGroups() {
        Map<String, List<Tuple<Predicate>>> compositeGroups = new ConcurrentHashMap<String, List<Tuple<Predicate>>>();
        for (Entry<String, List<Tuple<CompiledPredicateResult<?>>>> entry : this.createStandardGroups().entrySet()) {
            List<Tuple<CompiledPredicateResult<?>>> values = entry.getValue();
            values.sort((l, r) -> l.getFirst().compareTo(r.getFirst()));
            List<Predicate> groupPs = new ArrayList<Predicate>();
            for (Tuple<CompiledPredicateResult<?>> ele : values) {
                groupPs.add(ele.getLast().getPredicate());
            }
            this.mergerCompositeGroups(compositeGroups, entry.getKey(), groupPs);
        }
        return compositeGroups;
    }

    /**
     * 递归折叠复合 group, 直到只剩 default 根组.
     */
    private Map<String, List<Tuple<Predicate>>> createCompositeGroups(final Map<String, List<Tuple<Predicate>>> map) {
        if (map.isEmpty() || (1 == map.size() && map.containsKey(Constant.DEFAULT))) {
            return map;
        }
        // 复合组允许 group 嵌套 group, 这里反复折叠直到只剩 DEFAULT 为止,
        // 从而复用既有分组语义, 而不再保留另一套实现.
        Map<String, List<Tuple<Predicate>>> compositeGroups = new ConcurrentHashMap<String, List<Tuple<Predicate>>>();
        for (Entry<String, List<Tuple<Predicate>>> entry : map.entrySet()) {
            List<Tuple<Predicate>> values = entry.getValue();
            values.sort((l, r) -> l.getFirst().compareTo(r.getFirst()));
            List<Predicate> groupPs = new ArrayList<Predicate>();
            for (Tuple<Predicate> predicate : values) {
                groupPs.add(predicate.getLast());
            }
            this.mergerCompositeGroups(compositeGroups, entry.getKey(), groupPs);
        }
        return this.createCompositeGroups(compositeGroups);
    }

    /**
     * 产出当前 scope 下按 group 规则组装后的最终谓词列表.
     */
    public List<Predicate> toPredicateList() {
        Map<String, List<Tuple<Predicate>>> compositeGroups = this.createCompositeGroups(this.createCompositeGroups());
        if (compositeGroups.isEmpty()) {
            return Collections.emptyList();
        }
        List<Tuple<Predicate>> values = compositeGroups.get(Constant.DEFAULT);
        values.sort((l, r) -> l.getFirst().compareTo(r.getFirst()));
        List<Predicate> predicates = new ArrayList<Predicate>();
        for (Tuple<Predicate> ele : values) {
            predicates.add(ele.getLast());
        }
        return predicates;
    }

    /**
     * 将当前 scope 下的结果组装为可递归执行的 group 树.
     */
    public PredicateGroupSpec toPredicateGroupSpec() {
        Map<String, List<Tuple<PredicateGroupSpec>>> compositeGroups = this.createCompositeGroupSpecs(this.createCompositeGroupSpecs());
        if (compositeGroups.isEmpty() || !compositeGroups.containsKey(Constant.DEFAULT)) {
            return PredicateGroupSpec.empty(resolveRootJunction());
        }
        List<Tuple<PredicateGroupSpec>> values = compositeGroups.get(Constant.DEFAULT);
        values.sort((l, r) -> l.getFirst().compareTo(r.getFirst()));
        List<PredicateGroupSpec> groups = new ArrayList<PredicateGroupSpec>();
        for (Tuple<PredicateGroupSpec> tuple : values) {
            groups.add(tuple.getLast());
        }
        if (1 == groups.size()) {
            return groups.get(0);
        }
        return PredicateGroupSpec.of(resolveRootJunction(), Collections.<CompiledAnnotationSpec<?>>emptyList(), groups);
    }

    /**
     * 解析 default 根组使用的 junction 语义.
     */
    private PredicateGroupSpec.JunctionType resolveRootJunction() {
        return GroupJunctions.junction(this.getComputerType(Constant.DEFAULT, Constant.GLOBAL));
    }

    private Type getComputerType(final String scope, final String value) {
        return this.annotationCollection.getComputerType(scope, value);
    }

    /**
     * 先按 group 收集规格叶子, 用于构建 group 树而不是直接构建 Predicate.
     */
    private Map<String, List<Tuple<CompiledAnnotationSpec<?>>>> createStandardSpecGroups() {
        List<CompiledPredicateResult<?>> valid = this.results.stream().filter(it -> Objects.nonNull(it) && !it.isEmpty() && Objects.nonNull(it.getSpec()))
            .filter(it -> containsScope(it.getSpec())).collect(Collectors.toList());
        Map<String, List<Tuple<CompiledAnnotationSpec<?>>>> standardGroups = new ConcurrentHashMap<String, List<Tuple<CompiledAnnotationSpec<?>>>>();
        for (CompiledPredicateResult<?> result : valid) {
            for (GroupInfo groupInfo : result.getSpec().getEffectiveOptions().getGroups()) {
                String conditionName = groupInfo.value();
                if (!standardGroups.containsKey(conditionName)) {
                    standardGroups.put(conditionName, new ArrayList<Tuple<CompiledAnnotationSpec<?>>>());
                }
                standardGroups.get(conditionName).add(Tuple.of(groupInfo.order(), result.getSpec()));
            }
        }
        return standardGroups;
    }

    /**
     * 将同组规格叶子折叠成一层 leaf group.
     */
    private void mergerCompositeGroupSpecs(final Map<String, List<Tuple<PredicateGroupSpec>>> map, final String value,
        final List<CompiledAnnotationSpec<?>> groupSpecs) {
        this.routeCompositeGroupSpec(map, value, PredicateGroupSpec.leaf(GroupJunctions.junction(groupRuleType(value)), groupSpecs));
    }

    /**
     * 按 group 规则将当前 group 规格路由到其父级组.
     */
    private void routeCompositeGroupSpec(final Map<String, List<Tuple<PredicateGroupSpec>>> map, final String value, final PredicateGroupSpec groupSpec) {
        GroupComputerType mergeRule = groupRule(value);
        if (null == mergeRule) {
            if (!map.containsKey(Constant.DEFAULT)) {
                map.put(Constant.DEFAULT, new ArrayList<Tuple<PredicateGroupSpec>>());
            }
            map.get(Constant.DEFAULT).add(Tuple.of(0, groupSpec));
            return;
        }
        for (GroupInfo groupInfo : mergeRule.groups()) {
            String conditionName = groupInfo.value();
            if (!map.containsKey(conditionName)) {
                map.put(conditionName, new ArrayList<Tuple<PredicateGroupSpec>>());
            }
            map.get(conditionName).add(Tuple.of(groupInfo.order(), groupSpec));
        }
    }

    /**
     * 生成第一层复合 group 规格.
     */
    private Map<String, List<Tuple<PredicateGroupSpec>>> createCompositeGroupSpecs() {
        Map<String, List<Tuple<PredicateGroupSpec>>> compositeGroups = new ConcurrentHashMap<String, List<Tuple<PredicateGroupSpec>>>();
        for (Entry<String, List<Tuple<CompiledAnnotationSpec<?>>>> entry : this.createStandardSpecGroups().entrySet()) {
            List<Tuple<CompiledAnnotationSpec<?>>> values = entry.getValue();
            values.sort((l, r) -> l.getFirst().compareTo(r.getFirst()));
            List<CompiledAnnotationSpec<?>> groupSpecs = new ArrayList<CompiledAnnotationSpec<?>>();
            for (Tuple<CompiledAnnotationSpec<?>> ele : values) {
                groupSpecs.add(ele.getLast());
            }
            this.mergerCompositeGroupSpecs(compositeGroups, entry.getKey(), groupSpecs);
        }
        return compositeGroups;
    }

    /**
     * 递归折叠 group 规格树, 直到只剩 default 根组.
     */
    private Map<String, List<Tuple<PredicateGroupSpec>>> createCompositeGroupSpecs(final Map<String, List<Tuple<PredicateGroupSpec>>> map) {
        if (map.isEmpty() || (1 == map.size() && map.containsKey(Constant.DEFAULT))) {
            return map;
        }
        Map<String, List<Tuple<PredicateGroupSpec>>> compositeGroups = new ConcurrentHashMap<String, List<Tuple<PredicateGroupSpec>>>();
        for (Entry<String, List<Tuple<PredicateGroupSpec>>> entry : map.entrySet()) {
            List<Tuple<PredicateGroupSpec>> values = entry.getValue();
            values.sort((l, r) -> l.getFirst().compareTo(r.getFirst()));
            List<PredicateGroupSpec> children = new ArrayList<PredicateGroupSpec>();
            for (Tuple<PredicateGroupSpec> predicate : values) {
                children.add(predicate.getLast());
            }
            PredicateGroupSpec nested = PredicateGroupSpec.of(GroupJunctions.junction(groupRuleType(entry.getKey())),
                Collections.<CompiledAnnotationSpec<?>>emptyList(), children);
            this.routeCompositeGroupSpec(compositeGroups, entry.getKey(), nested);
        }
        return this.createCompositeGroupSpecs(compositeGroups);
    }

    /**
     * 读取指定 group 在当前 scope 下的合并规则.
     */
    private GroupComputerType groupRule(final String value) {
        return this.annotationCollection.getGroupComputerType(this.scope, value);
    }

    /**
     * 返回指定 group 的布尔连接方式, 未声明时回退 AND.
     */
    private Type groupRuleType(final String value) {
        return Optional.ofNullable(groupRule(value)).map(GroupComputerType::type).orElse(Type.AND);
    }

    /**
     * 判断当前规格是否声明参与当前 scope.
     */
    private boolean containsScope(final CompiledAnnotationSpec<?> spec) {
        if (Objects.isNull(spec)) {
            return false;
        }
        for (String declaredScope : spec.getEffectiveOptions().getScope()) {
            if (Objects.equals(declaredScope, this.scope)) {
                return true;
            }
        }
        return false;
    }

    private static class Tuple<V> {

        private final Integer first;

        private final V last;

        /**
         * 创建一条“排序键 + 值对象”的元组.
         */
        static <V> Tuple<V> of(final Integer first, final V last) {
            return new Tuple<V>(first, last);
        }

        Tuple(final Integer first, final V last) {
            this.first = first;
            this.last = last;
        }

        Integer getFirst() {
            return this.first;
        }

        V getLast() {
            return this.last;
        }

    }

}
