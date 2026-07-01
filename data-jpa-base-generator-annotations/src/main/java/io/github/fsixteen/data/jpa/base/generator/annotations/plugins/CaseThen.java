package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;

/**
 * {@link Cases} 分支命中后的执行配置。
 *
 * <p>
 * 该注解用于描述“分支命中后如何生成最终谓词”。canonical {@code op/right/extra}
 * 构成默认执行语言，可直接表达常见分支产出规则；若需要完全自定义的谓词产出，则通过
 * {@link #processor()} 接入显式 processor 扩展。若需要一次产出多条叶子谓词，
 * 则使用 {@link #group()}。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface CaseThen {

    /**
     * processor 扩展引用。
     *
     * <p>
     * 配置后，当前分支的实际谓词产出将由自定义 processor 接管。
     * </p>
     *
     * @return ProcessorRef
     */
    ProcessorRef processor() default @ProcessorRef();

    /**
     * 结构化 then 动作分组。
     *
     * <p>
     * 当未显式指定 {@link #processor()} 时，可通过该字段一次产出多条 canonical 叶子谓词，
     * 并按分组 junction 组合成最终分支谓词。
     * </p>
     *
     * @return CaseThenGroup
     */
    CaseThenGroup group() default @CaseThenGroup();

    /**
     * canonical 比较操作符。
     *
     * <p>
     * 当未显式指定 processor 时，分支命中后会直接按这里的 compare 规则构建谓词；
     * 若也未声明 canonical 表达式，则回退到内建默认分支语义。
     * </p>
     *
     * @return CompareOp
     */
    CompareOp op() default CompareOp.EQ;

    /**
     * 分支输出谓词的右侧 canonical 表达式。
     *
     * <p>
     * 该表达式会与当前分支生效的左侧表达式一起参与最终谓词构建。
     * 未显式配置时，默认回退为当前注解绑定字段的运行时值。
     * </p>
     *
     * @return Expr
     */
    Expr right() default @Expr(type = ExprType.AUTO);

    /**
     * 分支输出谓词的附加 canonical 表达式列表。
     *
     * <p>
     * 该字段用于承载除分支左侧表达式与 {@link #right()} 之外的额外操作数，
     * 典型场景包括 {@code BETWEEN} 这类需要第二个边界值的分支输出规则。
     * </p>
     *
     * @return Expr[]
     */
    Expr[] extra() default {};

}
