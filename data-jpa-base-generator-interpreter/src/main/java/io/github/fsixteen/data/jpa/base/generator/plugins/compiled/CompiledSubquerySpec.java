package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

/**
 * 子查询规格.
 *
 * <p>
 * 当前统一承接 {@code InTable}、{@code Exists}、{@code NotExists} 这类子查询能力.
 * 不同注解只在最终谓词外壳上存在差异, 例如 {@code in(subquery)}、{@code exists(subquery)}、
 * {@code not exists(subquery)}；子查询自身的 from/select/correlated-path/where
 * 结构已经统一收口到这里.
 * </p>
 *
 * <p>
 * 该对象只描述“子查询需要长成什么样”, 不负责编译或执行.
 * 真正的 where-group 构建由 {@link CompiledSubqueryGroupSpecs} 负责,
 * 最终执行则交给 {@link CompiledSubquerySupport}.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledSubquerySpec {

    private final io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode mode;

    private final Class<?> fromEntity;

    private final String selectPath;

    private final String sourcePath;

    private final String correlatedPath;

    private final CompiledAnnotationSpec<?> nestedPredicateSpec;

    private final PredicateGroupSpec predicateGroupSpec;

    private CompiledSubquerySpec(final io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode mode, final Class<?> fromEntity,
        final String selectPath, final String sourcePath, final String correlatedPath, final CompiledAnnotationSpec<?> nestedPredicateSpec,
        final PredicateGroupSpec predicateGroupSpec) {
        this.mode = mode;
        this.fromEntity = fromEntity;
        this.selectPath = selectPath;
        this.sourcePath = sourcePath;
        this.correlatedPath = correlatedPath;
        this.nestedPredicateSpec = nestedPredicateSpec;
        this.predicateGroupSpec = predicateGroupSpec;
    }

    /**
     * 创建仅包含单个嵌套叶子的子查询规格.
     */
    public static CompiledSubquerySpec of(final io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode mode, final Class<?> fromEntity,
        final String selectPath, final String sourcePath, final CompiledAnnotationSpec<?> nestedPredicateSpec) {
        return new CompiledSubquerySpec(mode, fromEntity, selectPath, sourcePath, null, nestedPredicateSpec, null);
    }

    /**
     * 创建包含嵌套叶子和 group 树的子查询规格.
     */
    public static CompiledSubquerySpec of(final io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode mode, final Class<?> fromEntity,
        final String selectPath, final String sourcePath, final CompiledAnnotationSpec<?> nestedPredicateSpec, final PredicateGroupSpec predicateGroupSpec) {
        return new CompiledSubquerySpec(mode, fromEntity, selectPath, sourcePath, null, nestedPredicateSpec, predicateGroupSpec);
    }

    /**
     * 创建包含关联路径的完整子查询规格.
     */
    public static CompiledSubquerySpec of(final io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode mode, final Class<?> fromEntity,
        final String selectPath, final String sourcePath, final String correlatedPath, final CompiledAnnotationSpec<?> nestedPredicateSpec,
        final PredicateGroupSpec predicateGroupSpec) {
        return new CompiledSubquerySpec(mode, fromEntity, selectPath, sourcePath, correlatedPath, nestedPredicateSpec, predicateGroupSpec);
    }

    /**
     * 返回子查询模式.
     */
    public io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode getMode() {
        return this.mode;
    }

    /**
     * 返回子查询 from 实体类型.
     */
    public Class<?> getFromEntity() {
        return this.fromEntity;
    }

    /**
     * 返回子查询 select 路径.
     */
    public String getSelectPath() {
        return this.selectPath;
    }

    /**
     * 返回外层与子查询共享的源路径.
     */
    public String getSourcePath() {
        return this.sourcePath;
    }

    /**
     * 返回嵌套叶子谓词规格.
     */
    public CompiledAnnotationSpec<?> getNestedPredicateSpec() {
        return this.nestedPredicateSpec;
    }

    /**
     * 返回子查询内部 group 树.
     */
    public PredicateGroupSpec getPredicateGroupSpec() {
        return this.predicateGroupSpec;
    }

    /**
     * 返回用于相关子查询的关联路径.
     */
    public String getCorrelatedPath() {
        return this.correlatedPath;
    }

}
