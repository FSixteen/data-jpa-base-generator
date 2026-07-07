package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.RuntimeValueResolver;

/**
 * tuple 数据源解析器.
 *
 * <p>
 * 该类型只负责决定 tuple 行集合从哪里读取：
 * 是当前注解宿主字段本身, 还是 {@code tupleField} 指向的外部属性路径.
 * 它不负责把读取结果转成稳定行模型.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
final class TupleSourceResolver {

    private TupleSourceResolver() {
    }

    /**
     * 解析 tuple 行集合的真实数据源.
     *
     * <p>
     * 当规格声明使用宿主字段本身作为 tuple 源时, 直接返回当前字段值；
     * 否则从参数对象上读取 `tupleField` 指向的外部路径.
     * </p>
     *
     * @param spec            tuple 谓词规格
     * @param args            当前请求或查询对象
     * @param ownerFieldValue 当前宿主字段值
     * @return 原始 tuple 数据源, 允许为 {@code null}
     */
    static Object resolveTupleSource(final CompiledTuplePredicateSpec spec, final Object args, final Object ownerFieldValue) {
        if (spec.usesHostFieldAsTupleSource()) {
            return ownerFieldValue;
        }
        return RuntimeValueResolver.readPath(args, spec.getTupleField());
    }

    /**
     * 判断当前 tuple 谓词是否应被启用.
     *
     * <p>
     * 若 tuple 数据源来自宿主字段本身, 则始终允许执行；
     * 否则会结合宿主字段值的布尔开关语义与 ignore 规则决定是否继续.
     * </p>
     *
     * @param spec            tuple 谓词规格
     * @param ownerFieldValue 当前宿主字段值
     * @return 当前 tuple 谓词可以进入后续执行阶段时返回 {@code true}
     */
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
