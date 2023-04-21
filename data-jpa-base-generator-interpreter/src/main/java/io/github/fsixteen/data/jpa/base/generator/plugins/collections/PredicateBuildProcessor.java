package io.github.fsixteen.data.jpa.base.generator.plugins.collections;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * 查询条件构建器.<br>
 * 
 * @author FSixteen
 * @since V1.0.0
 */
class PredicateBuildProcessor {

    /**
     * 类注解逻辑描述信息集合.<br>
     */
    private ComputerCollection computerCollection;

    /**
     * 范围查询分组.<br>
     */
    private String scope = Constant.DEFAULT;

    /**
     * {@link javax.persistence.criteria.CriteriaBuilder}实例.<br>
     */
    private CriteriaBuilder cb;

    PredicateBuildProcessor(ComputerCollection computerCollection) {
        this.computerCollection = computerCollection;
    }

    PredicateBuildProcessor(ComputerCollection computerCollection, CriteriaBuilder cb, String scope) {
        this.computerCollection = computerCollection;
        this.scope = scope;
        this.cb = cb;
    }

    /**
     * 指定范围查询分组.<br>
     * 
     * @param scope 范围查询分组
     * @return PredicateBuildProcessor
     */
    PredicateBuildProcessor withScope(String scope) {
        this.scope = scope;
        return this;
    }

    /**
     * 指定{@link javax.persistence.criteria.CriteriaBuilder}实例.<br>
     * 
     * @param cb {@link javax.persistence.criteria.CriteriaBuilder}实例
     * @return PredicateBuildProcessor
     */
    PredicateBuildProcessor withCriteriaBuilder(CriteriaBuilder cb) {
        this.cb = cb;
        return this;
    }

    /**
     * 标准逻辑描述信息(&lt;条件分组名称, &lt;条件顺序, 注解逻辑描述信息&gt;&gt;).<br>
     * 
     * @return Map&lt;String,
     *         List&lt;Tuple&lt;ComputerDescriptor&lt;Annotation&gt;&gt;&gt;&gt;
     */
    private Map<String, List<Tuple<ComputerDescriptor<Annotation>>>> createStandardGroups() {
        List<ComputerDescriptor<Annotation>> valid = this.computerCollection.getComputerDescriptors().stream()
                .filter(it -> Objects.nonNull(it) && !it.isEmpty() && it.containsScope(scope)).collect(Collectors.toList());
        Map<String, List<Tuple<ComputerDescriptor<Annotation>>>> standardGroups = new ConcurrentHashMap<>();
        for (ComputerDescriptor<Annotation> computerDescriptor : valid) {
            for (GroupInfo groupInfo : computerDescriptor.getAnnoDesc().getGroups()) {
                String conditionName = groupInfo.value();
                if (!standardGroups.containsKey(conditionName)) {
                    standardGroups.put(conditionName, new ArrayList<>());
                }
                standardGroups.get(conditionName).add(Tuple.of(groupInfo.order(), computerDescriptor));
            }
        }
        return standardGroups;
    }

    /**
     * 按照给定计算方式, 组装分组条件.<br>
     * 
     * @param map     复合逻辑描述信息
     * @param value   分组名称
     * @param groupPs 逻辑描述信息
     */
    private void mergerCompositeGroups(Map<String, List<Tuple<Predicate>>> map, String value, List<Predicate> groupPs) {
        // 按照给定计算方式, 组装分组条件.
        GroupComputerType groupComputerType = this.computerCollection.getGroupComputerType(scope, value);
        Predicate[] predicates = groupPs.toArray(new Predicate[groupPs.size()]);
        if (null == groupComputerType) {
            if (!map.containsKey(Constant.DEFAULT)) {
                map.put(Constant.DEFAULT, new ArrayList<>());
            }
            map.get(Constant.DEFAULT).add(Tuple.of(0, cb.and(predicates)));
        } else {
            for (GroupInfo groupInfo : groupComputerType.groups()) {
                String conditionName = groupInfo.value();
                if (!map.containsKey(conditionName)) {
                    map.put(conditionName, new ArrayList<>());
                }
                Predicate predicate = Type.OR == groupComputerType.type() ? cb.or(predicates) : cb.and(predicates);
                map.get(conditionName).add(Tuple.of(groupInfo.order(), predicate));
            }
        }
    }

    /**
     * 标准复合逻辑描述信息(&lt;条件分组名称, &lt;条件顺序, 逻辑描述信息&gt;&gt;).<br>
     * 
     * @return Map&lt;String, List&lt;Tuple&lt;Predicate&gt;&gt;&gt;
     */
    private Map<String, List<Tuple<Predicate>>> createCompositeGroups() {
        Map<String, List<Tuple<Predicate>>> compositeGroups = new ConcurrentHashMap<>();
        for (Entry<String, List<Tuple<ComputerDescriptor<Annotation>>>> entry : this.createStandardGroups().entrySet()) {
            // 排序分组条件, 并转换为 Predicate 集合.
            List<Tuple<ComputerDescriptor<Annotation>>> values = entry.getValue();
            values.sort((l, r) -> l.getFirst().compareTo(r.getFirst()));
            List<Predicate> groupPs = new ArrayList<>();
            for (Tuple<ComputerDescriptor<Annotation>> ele : values) {
                groupPs.add(ele.getLast().getPredicate());
            }
            this.mergerCompositeGroups(compositeGroups, entry.getKey(), groupPs);
        }
        return compositeGroups;
    }

    /**
     * 联合复合逻辑描述信息(&lt;条件分组名称, &lt;条件顺序, 逻辑描述信息&gt;&gt;).<br>
     * 
     * @param map 复合逻辑描述信息
     * @return Map&lt;String, List&lt;Tuple&lt;Predicate&gt;&gt;&gt;
     */
    private Map<String, List<Tuple<Predicate>>> createCompositeGroups(Map<String, List<Tuple<Predicate>>> map) {
        if (map.isEmpty() || (1 == map.size() && map.containsKey(Constant.DEFAULT))) {
            return map;
        }
        Map<String, List<Tuple<Predicate>>> compositeGroups = new ConcurrentHashMap<>();
        for (Entry<String, List<Tuple<Predicate>>> entry : map.entrySet()) {
            // 排序分组条件, 并转换为 Predicate 集合.
            List<Tuple<Predicate>> values = entry.getValue();
            values.sort((l, r) -> l.getFirst().compareTo(r.getFirst()));
            List<Predicate> groupPs = new ArrayList<>();
            for (Tuple<Predicate> predicate : values) {
                groupPs.add(predicate.getLast());
            }
            this.mergerCompositeGroups(compositeGroups, entry.getKey(), groupPs);
        }
        return this.createCompositeGroups(compositeGroups);
    }

    /**
     * 构建查询条件.<br>
     * 
     * @return List&lt;Predicate&gt;
     */
    private List<Predicate> compositePredicateList() {
        Map<String, List<Tuple<Predicate>>> compositeGroups = this.createCompositeGroups(this.createCompositeGroups());
        if (compositeGroups.isEmpty()) {
            return Collections.emptyList();
        }
        List<Tuple<Predicate>> values = compositeGroups.get(Constant.DEFAULT);
        values.sort((l, r) -> l.getFirst().compareTo(r.getFirst()));
        List<Predicate> predicates = new ArrayList<>();
        for (Tuple<Predicate> ele : values) {
            predicates.add(ele.getLast());
        }
        return predicates;
    }

    /**
     * 构建查询条件.<br>
     * 
     * @return List&lt;Predicate&gt;
     */
    public List<Predicate> toPredicate() {
        return this.compositePredicateList();
    }

    /**
     * 内部元组.
     * 
     * @author FSixteen
     * @since V1.0.0
     */
    private static class Tuple<V> {

        private Integer first;
        private V last;

        public static <V> Tuple<V> of(Integer first, V last) {
            return new Tuple<>(first, last);
        }

        public Tuple(Integer first, V last) {
            this.first = first;
            this.last = last;
        }

        public Integer getFirst() {
            return first;
        }

        public V getLast() {
            return last;
        }

    }

}
