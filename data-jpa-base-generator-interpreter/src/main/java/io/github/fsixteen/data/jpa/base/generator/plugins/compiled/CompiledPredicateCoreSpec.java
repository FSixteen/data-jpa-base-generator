package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.Arrays;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.PredicateOptions;

/**
 * 注解编译结果中的 canonical 谓词核心配置。
 *
 * <p>
 * 该对象只负责承载“如何比较”的最小信息：左右表达式、额外操作数、操作符和原始选项。
 * 这样 {@link CompiledAnnotationSpec} 可以把谓词语义与集合策略、字段反射元数据拆开管理。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledPredicateCoreSpec {

    private final Expr left;

    private final Expr right;

    private final Expr[] extra;

    private final PredicateOptions options;

    private final CompareOp op;

    private CompiledPredicateCoreSpec(final Expr left, final Expr right, final Expr[] extra, final PredicateOptions options, final CompareOp op) {
        this.left = left;
        this.right = right;
        this.extra = null == extra ? new Expr[0] : Arrays.copyOf(extra, extra.length);
        this.options = options;
        this.op = op;
    }

    /**
     * 创建一份仅包含 compare 核心语义的规格对象。
     */
    public static CompiledPredicateCoreSpec of(final Expr left, final Expr right, final Expr[] extra, final PredicateOptions options, final CompareOp op) {
        return new CompiledPredicateCoreSpec(left, right, extra, options, op);
    }

    /**
     * 返回左侧 canonical 表达式。
     */
    public Expr getLeft() {
        return this.left;
    }

    /**
     * 返回右侧 canonical 表达式。
     */
    public Expr getRight() {
        return this.right;
    }

    /**
     * 返回附加操作数表达式。
     */
    public Expr[] getExtra() {
        return Arrays.copyOf(this.extra, this.extra.length);
    }

    /**
     * 返回原始公共选项注解。
     */
    public PredicateOptions getOptions() {
        return this.options;
    }

    /**
     * 返回 compare 操作符。
     */
    public CompareOp getOp() {
        return this.op;
    }

}
