package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 谓词嵌套分组规格.
 *
 * <p>
 * 该模型现在承载“一个分组下有哪些注解规格、有哪些子分组”的结构信息,
 * 作为 compiled-only 方向上的稳定中间层, 供复杂对象、子查询和后续 exists/not exists
 * 能力共享.
 * </p>
 *
 * <p>
 * 它只表达树结构和 junction 语义, 不包含任何运行时上下文；
 * 真正的谓词执行由 {@link CompiledPredicateGroupResolver} 完成.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class PredicateGroupSpec {

    /**
     * 分组内部的布尔连接方式.
     */
    public enum JunctionType {
        AND, OR
    }

    private final JunctionType junctionType;

    private final List<CompiledAnnotationSpec<?>> annotations;

    private final List<PredicateGroupSpec> groups;

    private PredicateGroupSpec(final JunctionType junctionType, final List<CompiledAnnotationSpec<?>> annotations, final List<PredicateGroupSpec> groups) {
        this.junctionType = junctionType;
        this.annotations = Collections.unmodifiableList(new ArrayList<CompiledAnnotationSpec<?>>(annotations));
        this.groups = Collections.unmodifiableList(new ArrayList<PredicateGroupSpec>(groups));
    }

    /**
     * 创建一个同时包含注解叶子和子分组的 group 规格.
     */
    public static PredicateGroupSpec of(final JunctionType junctionType, final List<CompiledAnnotationSpec<?>> annotations,
        final List<PredicateGroupSpec> groups) {
        return new PredicateGroupSpec(junctionType, annotations, groups);
    }

    /**
     * 创建一个仅包含当前层注解叶子的 group 规格.
     */
    public static PredicateGroupSpec leaf(final JunctionType junctionType, final List<CompiledAnnotationSpec<?>> annotations) {
        return new PredicateGroupSpec(junctionType, annotations, Collections.<PredicateGroupSpec>emptyList());
    }

    /**
     * 创建一个空分组.
     */
    public static PredicateGroupSpec empty(final JunctionType junctionType) {
        return new PredicateGroupSpec(junctionType, Collections.<CompiledAnnotationSpec<?>>emptyList(), Collections.<PredicateGroupSpec>emptyList());
    }

    /**
     * 返回当前分组的布尔连接方式.
     */
    public JunctionType getJunctionType() {
        return this.junctionType;
    }

    /**
     * 返回当前分组直接持有的注解叶子.
     */
    public List<CompiledAnnotationSpec<?>> getAnnotations() {
        return this.annotations;
    }

    /**
     * 返回当前分组的子分组列表.
     */
    public List<PredicateGroupSpec> getGroups() {
        return this.groups;
    }

    /**
     * 判断当前分组是否既无叶子也无子组.
     */
    public boolean isEmpty() {
        return this.annotations.isEmpty() && this.groups.isEmpty();
    }

}
