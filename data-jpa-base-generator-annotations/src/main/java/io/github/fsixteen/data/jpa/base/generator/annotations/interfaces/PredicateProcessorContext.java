package io.github.fsixteen.data.jpa.base.generator.annotations.interfaces;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare;

/**
 * {@link PredicateProcessor} 的单次执行上下文。
 *
 * <p>
 * 该上下文将 {@code @Cases} 分支命中后的所有运行期信息收口为一个不可变对象，通过
 * {@link PredicateProcessor#create(PredicateProcessorContext)} 传递给自定义
 * processor 实现，避免 SPI 接口暴露多组重载回调。
 * </p>
 *
 * <h3>装配时机</h3>
 * <ol>
 * <li>编译阶段（{@code CompiledSpecializedSpecs.cases()}）：将 {@code @CaseWhen} 和
 * {@code @CaseThen} 分别编译为 {@code CompiledAnnotationSpec&lt;Compare&gt;}</li>
 * <li>运行期（{@code CompiledCasesSupport}）：分支命中后，将编译后的规格还原为注解模型
 * {@link io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare}，
 * 连同请求参数对象、字段值、JPA 运行时对象一起打包进此上下文</li>
 * </ol>
 *
 * <h3>canonicalWhen 与 canonicalThen 的取值关系</h3>
 * <table border="1">
 * <caption>上下文字段非空条件</caption>
 * <tr>
 * <th>字段</th>
 * <th>空值条件</th>
 * </tr>
 * <tr>
 * <td>{@code canonicalWhen}</td>
 * <td>当 {@code @CaseWhen} 未声明 canonical
 * {@code op/left/right/extra} 时（即使用 {@code PredicateRef} 或默认 always-match
 * 模式），该字段为 {@code null}</td>
 * </tr>
 * <tr>
 * <td>{@code canonicalThen}</td>
 * <td>当 {@code @CaseThen} 未声明 canonical then
 * 规则且未设置 {@code @ProcessorRef} 时，回退到等值默认值；若仅设置 processor 而未配置
 * canonical 操作，该字段仍可为 {@code null}</td>
 * </tr>
 * </table>
 *
 * <h3>使用建议</h3>
 * <ul>
 * <li>processor 实现应优先使用 {@link #getCanonicalThen()} 中的
 * {@code op / right / extra} 来构建谓词，而不是硬编码判断逻辑 —— 这样业务组合注解可以
 * 通过覆写这些字段来修改分支行为，而不必重写 processor</li>
 * <li>若 {@code canonicalThen} 为 {@code null}，processor 应自行提供完整的默认回退逻辑</li>
 * <li>{@code args} 是原始的请求参数对象，可用于读取不在分支字段路径上的额外属性</li>
 * </ul>
 *
 * @author FSixteen
 * @since 1.0.3
 * @see PredicateProcessor
 * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseThen
 * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseWhen
 * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Cases
 */
public final class PredicateProcessorContext {

    /**
     * 分支命中条件的 canonical 规则。
     * <p>
     * 对应
     * {@link io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseWhen}
     * 中声明的 {@code op / left / right / extra}。当分支 {@code @CaseWhen} 未声明 canonical
     * 条件（即使用 {@code @PredicateRef} 或默认 always-match）时此字段为 {@code null}。
     * </p>
     */
    private final Compare canonicalWhen;

    /**
     * 分支命中的执行规则。
     * <p>
     * 对应
     * {@link io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseThen}
     * 中声明的 {@code op / right / extra}。当分支同时配置了 {@code @ProcessorRef} 且未声明
     * canonical then 规则时，此字段为 {@code null}，processor 实现应自行提供回退。
     * </p>
     */
    private final Compare canonicalThen;

    /**
     * 原始请求参数对象。
     * <p>
     * 即调用方传入的 query model 实例。processor 可通过该对象读取不在当前分支路径上的额外属性，
     * 用于构建跨字段的复杂谓词。
     * </p>
     */
    private final Object args;

    /**
     * 当前分支作用的 JPA 属性路径。
     * <p>
     * 编译阶段由 {@code @Case.left()} 或外层 {@code @Cases.left()} 或注解所在字段名推导得出。
     * processor 可使用该路径通过 {@link #getRoot()} 编译 JPA {@code Path}。
     * </p>
     */
    private final String fieldName;

    /**
     * 当前字段的运行时值。
     * <p>
     * 该值已在运行期经过 {@code PredicateOptions} 的 trim / ignoreNull / ignoreEmpty /
     * ignoreBlank 预处理。processor 可直接用于 JPA 参数绑定。
     * </p>
     */
    private final Object fieldValue;

    /**
     * JPA {@link Root}，代表查询的根实体。
     */
    private final Root<?> root;

    /**
     * JPA {@link AbstractQuery}，代表当前查询对象。
     * <p>
     * 子查询场景下可能为子查询 {@code Subquery}，且可能为 {@code null}，
     * processor 需做好防御性判断。
     * </p>
     */
    private final AbstractQuery<?> query;

    /**
     * JPA {@link CriteriaBuilder}，用于构建 {@code Predicate}。
     */
    private final CriteriaBuilder criteriaBuilder;

    /**
     * 全参构造。
     *
     * @param canonicalWhen   分支命中条件的 canonical 规则（可为 null）
     * @param canonicalThen   分支命中后的执行规则（可为 null）
     * @param args            原始请求参数对象
     * @param fieldName       当前分支作用的 JPA 属性路径
     * @param fieldValue      当前字段的运行时值（已预处理）
     * @param root            JPA Root
     * @param query           JPA AbstractQuery（子查询场景可能为 null）
     * @param criteriaBuilder JPA CriteriaBuilder
     */
    private PredicateProcessorContext(final Compare canonicalWhen, final Compare canonicalThen, final Object args, final String fieldName,
        final Object fieldValue, final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder criteriaBuilder) {
        this.canonicalWhen = canonicalWhen;
        this.canonicalThen = canonicalThen;
        this.args = args;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.root = root;
        this.query = query;
        this.criteriaBuilder = criteriaBuilder;
    }

    /**
     * 工厂方法：创建 {@link PredicateProcessorContext} 实例。
     * <p>
     * 由 {@code CompiledCasesSupport} 在分支命中后调用。调用方需确保 {@code canonicalThen}
     * 对应的编译规格已在 {@code fieldName} 路径上准备好。
     * </p>
     *
     * @param canonicalWhen   分支命中条件的 canonical 规则，可为 null
     * @param canonicalThen   分支命中后的执行规则，可为 null
     * @param args            原始请求参数对象，不可为 null
     * @param fieldName       当前分支作用的 JPA 属性路径
     * @param fieldValue      当前字段的运行时值（已预处理）
     * @param root            JPA Root
     * @param query           JPA AbstractQuery，子查询场景可能为 null
     * @param criteriaBuilder JPA CriteriaBuilder
     * @return PredicateProcessorContext 实例
     */
    public static PredicateProcessorContext of(final Compare canonicalWhen, final Compare canonicalThen, final Object args, final String fieldName,
        final Object fieldValue, final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder criteriaBuilder) {
        return new PredicateProcessorContext(canonicalWhen, canonicalThen, args, fieldName, fieldValue, root, query, criteriaBuilder);
    }

    /**
     * 获取分支命中条件的 canonical 规则。
     *
     * @return {@link Compare} 注解，若分支未声明 canonical when 则返回 {@code null}
     */
    public Compare getCanonicalWhen() {
        return this.canonicalWhen;
    }

    /**
     * 获取分支命中后的执行规则。
     * <p>
     * processor 应优先使用此字段的 {@code op / right / extra} 来构建谓词；
     * 若返回 {@code null}，processor 需自行提供完整的默认回退逻辑。
     * </p>
     *
     * @return {@link Compare} 注解，若分支既未配置 processor 也未声明 canonical then 则返回
     *         {@code null}
     */
    public Compare getCanonicalThen() {
        return this.canonicalThen;
    }

    /**
     * 获取原始请求参数对象。
     * <p>
     * 可用于读取不在 {@link #getFieldName()} 路径上的额外属性，例如跨字段比较或
     * 关联实体查询的场景。
     * </p>
     *
     * @return 请求参数对象
     */
    public Object getArgs() {
        return this.args;
    }

    /**
     * 获取当前分支作用的 JPA 属性路径。
     * <p>
     * 该路径由编译阶段从 {@code @Case.left()} 或外层 {@code @Cases.left()} 或注解所在字段名
     * 推导得出。processor 可通过 {@code root.get(fieldName)} 获取对应 JPA {@code Path}。
     * </p>
     *
     * @return JPA 属性路径字符串
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * 获取当前字段的运行时值。
     * <p>
     * 该值已在运行期经过 {@code PredicateOptions} 的 trim / ignoreNull / ignoreEmpty /
     * ignoreBlank 预处理。processor 可直接用于 JPA 参数绑定，无需重复校验。
     * </p>
     *
     * @return 预处理后的字段运行时值
     */
    public Object getFieldValue() {
        return this.fieldValue;
    }

    /**
     * 获取 JPA {@link Root}，代表查询的根实体。
     *
     * @return JPA Root
     */
    public Root<?> getRoot() {
        return this.root;
    }

    /**
     * 获取 JPA {@link AbstractQuery}。
     * <p>
     * 子查询场景下可能为 {@code Subquery}，且可能为 {@code null}。
     * Processor 在使用前应做 {@code Objects.isNull()} 检查，避免 NPE。
     * </p>
     *
     * @return JPA AbstractQuery，可能为 {@code null}
     */
    public AbstractQuery<?> getQuery() {
        return this.query;
    }

    /**
     * 获取 JPA {@link CriteriaBuilder}，用于构建 {@code Predicate}。
     *
     * @return JPA CriteriaBuilder
     */
    public CriteriaBuilder getCriteriaBuilder() {
        return this.criteriaBuilder;
    }

}
