package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.RuntimeValueResolver;

/**
 * tuple 数据源解析器。
 *
 * <p>
 * 该类型只负责决定 tuple 行集合从哪里读取：
 * 是当前注解宿主字段本身，还是 {@code tupleField} 指向的外部属性路径。
 * 它不负责把读取结果转成稳定行模型。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
final class TupleSourceResolver {

    private TupleSourceResolver() {
    }

    static Object resolveTupleSource(final CompiledTuplePredicateSpec spec, final Object args, final Object ownerFieldValue) {
        if (spec.usesHostFieldAsTupleSource()) {
            return ownerFieldValue;
        }
        return RuntimeValueResolver.readPath(args, spec.getTupleField());
    }

    static boolean isEnabled(final CompiledTuplePredicateSpec spec, final Object ownerFieldValue) {
        if (spec.usesHostFieldAsTupleSource()) {
            return true;
        }
        if (Objects.isNull(ownerFieldValue)) {
            return false;
        }
        if (ownerFieldValue instanceof Boolean) {
            return Objects.equals(Boolean.TRUE, ownerFieldValue);
        }
        return !spec.getOwnerSpec().shouldIgnore(ownerFieldValue);
    }

}
