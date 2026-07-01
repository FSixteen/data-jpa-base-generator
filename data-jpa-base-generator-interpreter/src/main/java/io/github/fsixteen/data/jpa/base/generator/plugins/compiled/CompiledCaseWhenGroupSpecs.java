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
 * {@link CaseWhenGroup} 编译器。
 *
 * <p>
 * 该类型把 case-when 的结构化命中树转成统一的 {@link PredicateGroupSpec}，
 * 供运行时分支命中判断复用。
 * </p>
 */
public final class CompiledCaseWhenGroupSpecs {

    private CompiledCaseWhenGroupSpecs() {
    }

    public static PredicateGroupSpec compile(final CompiledAnnotationSpec<?> ownerSpec, final CaseWhenGroup group) {
        if (null == group) {
            return PredicateGroupSpec.empty(PredicateGroupSpec.JunctionType.AND);
        }
        return compileNode(ownerSpec, group.junction(), group.compare(), group.range(), group.membership(), group.nullCheck(), group.groups(),
            child -> compile(ownerSpec, child));
    }

    public static PredicateGroupSpec compile(final CompiledAnnotationSpec<?> ownerSpec, final NestedCaseWhenGroup group) {
        if (null == group) {
            return PredicateGroupSpec.empty(PredicateGroupSpec.JunctionType.AND);
        }
        return compileNode(ownerSpec, group.junction(), group.compare(), group.range(), group.membership(), group.nullCheck(), group.groups(),
            child -> compile(ownerSpec, child));
    }

    public static PredicateGroupSpec compile(final CompiledAnnotationSpec<?> ownerSpec, final DeepCaseWhenGroup group) {
        if (null == group) {
            return PredicateGroupSpec.empty(PredicateGroupSpec.JunctionType.AND);
        }
        return compileLeaf(ownerSpec, group.junction(), group.compare(), group.range(), group.membership(), group.nullCheck());
    }

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

    private static PredicateGroupSpec compileLeaf(final CompiledAnnotationSpec<?> ownerSpec, final GroupComputerType.Type junction, final Annotation[] compare,
        final Annotation[] range, final Annotation[] membership, final Annotation[] nullCheck) {
        List<CompiledAnnotationSpec<?>> annotations = new ArrayList<CompiledAnnotationSpec<?>>();
        collectAnnotations(annotations, ownerSpec, compare, range, membership, nullCheck);
        if (annotations.isEmpty()) {
            return PredicateGroupSpec.empty(GroupJunctions.junction(junction));
        }
        return PredicateGroupSpec.leaf(GroupJunctions.junction(junction), annotations);
    }

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
