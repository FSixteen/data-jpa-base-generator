package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseWhenGroup;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.DeepCaseWhenGroup;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NestedCaseWhenGroup;

/**
 * {@link CaseWhenGroup} 编译器.
 *
 * <p>
 * 该类型把 case-when 的结构化命中树转成统一的 {@link PredicateGroupSpec},
 * 供运行时分支命中判断复用.
 * </p>
 */
public final class CompiledCaseWhenGroupSpecs {

    private CompiledCaseWhenGroupSpecs() {
    }

    /**
     * 编译顶层 case-when group.
     *
     * @param ownerSpec 宿主注解规格
     * @param group     顶层 when-group
     * @return 编译后的分组规格；输入为空时返回空 AND 分组
     */
    public static PredicateGroupSpec compile(final CompiledAnnotationSpec<?> ownerSpec, final CaseWhenGroup group) {
        if (null == group) {
            return PredicateGroupSpec.empty(PredicateGroupSpec.JunctionType.AND);
        }
        return compileNode(ownerSpec, group.junction(), group.compare(), group.range(), group.membership(), group.nullCheck(), group.groups(),
            child -> compile(ownerSpec, child));
    }

    /**
     * 编译第二层 case-when group.
     *
     * @param ownerSpec 宿主注解规格
     * @param group     第二层 when-group
     * @return 编译后的分组规格；输入为空时返回空 AND 分组
     */
    public static PredicateGroupSpec compile(final CompiledAnnotationSpec<?> ownerSpec, final NestedCaseWhenGroup group) {
        if (null == group) {
            return PredicateGroupSpec.empty(PredicateGroupSpec.JunctionType.AND);
        }
        return compileNode(ownerSpec, group.junction(), group.compare(), group.range(), group.membership(), group.nullCheck(), group.groups(),
            child -> compile(ownerSpec, child));
    }

    /**
     * 编译最深层 case-when group.
     *
     * @param ownerSpec 宿主注解规格
     * @param group     最深层 when-group
     * @return 编译后的叶子分组规格；输入为空时返回空 AND 分组
     */
    public static PredicateGroupSpec compile(final CompiledAnnotationSpec<?> ownerSpec, final DeepCaseWhenGroup group) {
        if (null == group) {
            return PredicateGroupSpec.empty(PredicateGroupSpec.JunctionType.AND);
        }
        return compileLeaf(ownerSpec, group.junction(), group.compare(), group.range(), group.membership(), group.nullCheck());
    }

    /**
     * 编译既可能包含叶子也可能包含子组的 when-group 节点.
     *
     * @param ownerSpec     宿主注解规格
     * @param junction      分组连接方式
     * @param compare       compare 注解数组
     * @param range         range 注解数组
     * @param membership    membership 注解数组
     * @param nullCheck     null-check 注解数组
     * @param groups        子组数组
     * @param childCompiler 子组编译函数
     * @param <G>           子组注解类型
     * @return 编译后的分组规格
     */
    private static <G> PredicateGroupSpec compileNode(final CompiledAnnotationSpec<?> ownerSpec, final GroupComputerType.Type junction,
        final Annotation[] compare, final Annotation[] range, final Annotation[] membership, final Annotation[] nullCheck, final G[] groups,
        final Function<G, PredicateGroupSpec> childCompiler) {
        List<CompiledAnnotationSpec<?>> annotations = new ArrayList<CompiledAnnotationSpec<?>>();
        List<PredicateGroupSpec> children = new ArrayList<PredicateGroupSpec>();
        collectAnnotations(annotations, ownerSpec, compare, range, membership, nullCheck);
        if (null != groups) {
            for (G child : groups) {
                PredicateGroupSpec childGroup = childCompiler.apply(child);
                if (!childGroup.isEmpty()) {
                    children.add(childGroup);
                }
            }
        }
        if (annotations.isEmpty() && children.isEmpty()) {
            return PredicateGroupSpec.empty(GroupJunctions.junction(junction));
        }
        return PredicateGroupSpec.of(GroupJunctions.junction(junction), annotations, children);
    }

    /**
     * 编译只包含叶子注解的最深层 when-group.
     *
     * @param ownerSpec  宿主注解规格
     * @param junction   分组连接方式
     * @param compare    compare 注解数组
     * @param range      range 注解数组
     * @param membership membership 注解数组
     * @param nullCheck  null-check 注解数组
     * @return 编译后的叶子分组规格
     */
    private static PredicateGroupSpec compileLeaf(final CompiledAnnotationSpec<?> ownerSpec, final GroupComputerType.Type junction, final Annotation[] compare,
        final Annotation[] range, final Annotation[] membership, final Annotation[] nullCheck) {
        List<CompiledAnnotationSpec<?>> annotations = new ArrayList<CompiledAnnotationSpec<?>>();
        collectAnnotations(annotations, ownerSpec, compare, range, membership, nullCheck);
        if (annotations.isEmpty()) {
            return PredicateGroupSpec.empty(GroupJunctions.junction(junction));
        }
        return PredicateGroupSpec.leaf(GroupJunctions.junction(junction), annotations);
    }

    /**
     * 将同层原始注解统一编译为叶子规格并收集到目标列表.
     *
     * @param target    目标收集列表
     * @param ownerSpec 宿主注解规格
     * @param groups    同层原始注解分组数组
     */
    private static void collectAnnotations(final List<CompiledAnnotationSpec<?>> target, final CompiledAnnotationSpec<?> ownerSpec,
        final Annotation[]... groups) {
        if (null == groups) {
            return;
        }
        for (Annotation[] annotations : groups) {
            if (null == annotations || 0 == annotations.length) {
                continue;
            }
            for (Annotation annotation : annotations) {
                target.add(CompiledAnnotationSpec.of(ownerSpec.getObjClass(), annotation, ownerSpec.getValueField(), ownerSpec.getEffectiveOptions()));
            }
        }
    }

}
