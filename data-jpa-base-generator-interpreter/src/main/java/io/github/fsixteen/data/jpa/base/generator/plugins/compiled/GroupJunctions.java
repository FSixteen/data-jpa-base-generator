package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type;

/**
 * 公开分组规则类型到 compiled 谓词分组语义的桥接器。
 *
 * <p>
 * `GroupComputerType.Type` 仍是公开注解层的一部分，但 compiled 主链路内部尽量只消费
 * `PredicateGroupSpec.JunctionType` 这一最终语义。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
final class GroupJunctions {

    private GroupJunctions() {
    }

    /**
     * 将注解层 group type 映射为 compiled group junction。
     */
    static PredicateGroupSpec.JunctionType junction(final Type type) {
        return Type.OR == type ? PredicateGroupSpec.JunctionType.OR : PredicateGroupSpec.JunctionType.AND;
    }

    /**
     * 将完整 group 规则映射为 compiled group junction。
     */
    static PredicateGroupSpec.JunctionType junction(final GroupComputerType rule) {
        return null == rule ? PredicateGroupSpec.JunctionType.AND : junction(rule.type());
    }

    /**
     * 判断指定 group 规则是否为 OR 语义。
     */
    static boolean isOr(final GroupComputerType rule) {
        return null != rule && Type.OR == rule.type();
    }

}
