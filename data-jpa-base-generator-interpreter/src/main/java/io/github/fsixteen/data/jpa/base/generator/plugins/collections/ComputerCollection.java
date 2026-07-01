package io.github.fsixteen.data.jpa.base.generator.plugins.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.CompiledAnnotationSpec;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.CompiledPredicateAssembler;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.CompiledPredicateResult;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.PredicateBuildTarget;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.PredicateGroupSpec;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;
import io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProvider;
import io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProviderRegistry;

/**
 * 运行期构建结果集合。
 *
 * <p>
 * 该对象承接 {@link AnnotationCollection} 的 compiled 注解规格，
 * 结合具体的请求参数对象、JPA {@code Root}/{@code CriteriaBuilder} 等运行期上下文，
 * 生成 selection 或 existence 目标下的 {@link CompiledPredicateResult} 集合。
 * </p>
 *
 * <p>
 * 对外它仍保留历史上的稳定 API 面，例如 {@link #getPredicate(CriteriaBuilder)}、
 * {@link #getPredicateArray(CriteriaBuilder)} 和
 * {@link #getPredicateGroupSpec()}；
 * 但内部实际构建已经完全收敛到 compiled provider 和 compiled assembler。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public final class ComputerCollection {

    private final PredicateBuildTarget target;

    private final AnnotationCollection annotationCollection;

    private final Collection<CompiledPredicateResult<?>> predicateResults = new ArrayList<CompiledPredicateResult<?>>();

    private ComputerCollection(final PredicateBuildTarget target, final AnnotationCollection ac,
        final Collection<CompiledPredicateResult<?>> predicateResults) {
        this.target = Optional.ofNullable(target).orElse(PredicateBuildTarget.SELECTION);
        this.annotationCollection = ac;
        Optional.ofNullable(predicateResults).ifPresent(it -> it.forEach(this.predicateResults::add));
    }

    /**
     * 返回公开边界上的构建类型。
     *
     * @return BuilderType
     */
    public BuilderType getType() {
        return this.target.toBuilderType();
    }

    public boolean isSelection() {
        return this.target.isSelection();
    }

    public boolean isExistence() {
        return this.target.isExistence();
    }

    /**
     * 返回本次运行期构建所基于的注解集合。
     *
     * @return AnnotationCollection
     */
    public AnnotationCollection getAnnotationCollection() {
        return this.annotationCollection;
    }

    /**
     * 获取指定 scope 下的分组声明映射.<br>
     * 
     * @param scope 范围查询分组名称
     * @see AnnotationCollection#getGroupComputerType(String)
     * @return Map&lt;String, GroupComputerType&gt;
     */
    public Map<String, GroupComputerType> getGroupComputerTypeMap(String scope) {
        return this.annotationCollection.getGroupComputerType(scope);
    }

    /**
     * 获取指定 scope 与 group 的分组合并类型.<br>
     * 
     * @param scope 范围查询分组名称
     * @param value 条件分组名称
     * @return GroupComputerType
     */
    public Type getComputerType(String scope, String value) {
        return this.annotationCollection.getComputerType(scope, value);
    }

    /**
     * 获取指定 scope 与 group 的分组声明.<br>
     * 
     * @param scope 范围查询分组名称
     * @param value 条件分组名称
     * @see AnnotationCollection#getGroupComputerType(String, String)
     * @return GroupComputerType
     */
    public GroupComputerType getGroupComputerType(String scope, String value) {
        return this.annotationCollection.getGroupComputerType(scope, value);
    }

    /**
     * 获取指定 scope 下的分组声明数组.<br>
     * 
     * @param scope 范围查询分组名称
     * @return GroupComputerType[];
     */
    public GroupComputerType[] getGroupComputerTypes(String scope) {
        return this.annotationCollection.getGroupComputerTypes(scope);
    }

    /**
     * 获取全部分组声明数组.<br>
     * 
     * @return GroupComputerType[];
     */
    public GroupComputerType[] getGroupComputerTypes() {
        return this.annotationCollection.getGroupComputerTypes();
    }

    /**
     * compiled 主链路下的谓词结果集合。<br>
     *
     * @return compiled 谓词结果
     */
    public Collection<CompiledPredicateResult<?>> getPredicateResults() {
        return this.predicateResults;
    }

    /**
     * 获取{@link javax.persistence.criteria.Predicate}.<br>
     * 
     * @param cb 见{@link javax.persistence.criteria.CriteriaBuilder}
     * @return Predicate
     */
    public Predicate getPredicate(final CriteriaBuilder cb) {
        if (Type.OR == this.getComputerType(Constant.DEFAULT, Constant.GLOBAL)) {
            return cb.or(this.getPredicateArray(cb, Constant.DEFAULT));
        }
        return cb.and(this.getPredicateArray(cb, Constant.DEFAULT));
    }

    /**
     * 获取{@link javax.persistence.criteria.Predicate}.<br>
     * 
     * @param cb 见{@link javax.persistence.criteria.CriteriaBuilder}
     * @return List&lt;Predicate&gt;
     */
    public List<Predicate> getPredicateList(final CriteriaBuilder cb) {
        return this.getPredicateList(cb, Constant.DEFAULT);
    }

    /**
     * 获取{@link javax.persistence.criteria.Predicate}.<br>
     * 
     * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}
     * @param scope 范围查询分组
     * @return List&lt;Predicate&gt;
     */
    public List<Predicate> getPredicateList(final CriteriaBuilder cb, final String scope) {
        // 统一交给 compiled 组装器做分组与合并，避免内部再维护第二套 group 逻辑。
        return CompiledPredicateAssembler.of(this.annotationCollection, new ArrayList<CompiledPredicateResult<?>>(this.predicateResults), cb, scope)
            .toPredicateList();
    }

    /**
     * 获取{@link javax.persistence.criteria.Predicate}.<br>
     * 
     * @param cb 见{@link javax.persistence.criteria.CriteriaBuilder}
     * @return Predicate[]
     */
    public Predicate[] getPredicateArray(final CriteriaBuilder cb) {
        return this.getPredicateArray(cb, Constant.DEFAULT);
    }

    /**
     * 获取{@link javax.persistence.criteria.Predicate}.<br>
     * 
     * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}
     * @param scope 范围查询分组
     * @return Predicate[]
     */
    public Predicate[] getPredicateArray(final CriteriaBuilder cb, final String scope) {
        List<Predicate> predicates = this.getPredicateList(cb, scope);
        return predicates.toArray(new Predicate[predicates.size()]);
    }

    /**
     * 获取 compiled 主链路下的谓词分组树。<br>
     *
     * <p>
     * 该方法不会改变现有 {@link #getPredicate(CriteriaBuilder)} 的对外行为，
     * 但为复杂对象、子查询和后续 exists/not exists 能力提供统一的分组结构出口。
     * </p>
     *
     * @return 分组树
     */
    public PredicateGroupSpec getPredicateGroupSpec() {
        return this.getPredicateGroupSpec(Constant.DEFAULT);
    }

    /**
     * 获取指定 scope 的 compiled 谓词分组树。<br>
     *
     * @param scope 范围查询分组
     * @return 分组树
     */
    public PredicateGroupSpec getPredicateGroupSpec(final String scope) {
        return CompiledPredicateAssembler.of(this.annotationCollection, new ArrayList<CompiledPredicateResult<?>>(this.predicateResults), null, scope)
            .toPredicateGroupSpec();
    }

    /**
     * 判断当前构建结果是否为空.<br>
     * 
     * @return boolean
     */
    public boolean isEmpty() {
        return this.predicateResults.isEmpty();
    }

    /**
     * {@link ComputerCollection} 构造器.
     * 
     * @author FSixteen
     * @since 1.0.0
     */
    public static class Builder {

        private static final Logger LOG = LoggerFactory.getLogger(Builder.class);

        private AnnotationCollection annotationCollection;

        private Object args;

        private Root<?> root;

        private AbstractQuery<?> query;

        private CriteriaBuilder cb;

        private Builder() {
        }

        public static Builder of() {
            return new Builder();
        }

        /**
         * 设置{@link AnnotationCollection}实体.
         * 
         * @param ac {@link AnnotationCollection} 实体
         * @return Builder
         */
        public Builder withAnnotationCollection(AnnotationCollection ac) {
            this.annotationCollection = ac;
            return this;
        }

        /**
         * 设置实体.<br>
         * 
         * @param args 计算实体.
         * @return Builder
         */
        public Builder withArgs(Object args) {
            this.args = args;
            return this;
        }

        /**
         * 设置构建{@link javax.persistence.criteria.Predicate}所需要的{@link javax.persistence.criteria.Root},
         * {@link javax.persistence.criteria.AbstractQuery}(
         * {@link javax.persistence.criteria.CriteriaQuery} ),
         * {@link javax.persistence.criteria.CriteriaBuilder}.<br>
         * 
         * @param root  见{@link javax.persistence.criteria.Root}
         * @param query 见{@link javax.persistence.criteria.AbstractQuery}(
         *              {@link javax.persistence.criteria.CriteriaQuery} )
         * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}
         * @return Builder
         */
        public Builder withSpecification(Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb) {
            this.root = root;
            this.query = query;
            this.cb = cb;
            return this;
        }

        /**
         * 创建 compiled predicate 结果。
         *
         * @param spec    compiled 注解规格
         * @param results compiled 结果容器
         * @return Builder
         */
        private Builder addPredicateResult(final CompiledAnnotationSpec<?> spec, final Collection<CompiledPredicateResult<?>> results) {
            try {
                // 主路径现在统一只接受 compiled provider。
                // built-in 注解、SPI 注解以及 @Constraint 自定义注解都会先收敛到 registry。
                CompiledPredicateProvider provider = CompiledPredicateProviderRegistry.require(spec.getAnnotationType());
                results.add(CompiledPredicateResult.of(spec, provider.create(spec, this.args, this.root, this.query, this.cb)));
            } catch (IllegalArgumentException e) {
                LOG.error(e.getMessage(), e);
            }
            return this;
        }

        /**
         * 批量创建 compiled predicate 结果。
         *
         * @param specs   compiled 注解规格容器
         * @param results compiled 结果容器
         * @return Builder
         */
        private Builder addPredicateResults(final Collection<CompiledAnnotationSpec<?>> specs, final Collection<CompiledPredicateResult<?>> results) {
            specs.forEach(it -> this.addPredicateResult(it, results));
            return this;
        }

        /**
         * 按公开边界上的 {@link BuilderType} 创建运行期构建结果。
         *
         * <p>
         * 该方法主要服务于既有外部调用链；进入内部后会立即映射为
         * {@link PredicateBuildTarget}。
         * </p>
         *
         * @param type 外部稳定入口仍使用的构建类型
         * @return ComputerCollection
         */
        public ComputerCollection build(BuilderType type) {
            return this.build(PredicateBuildTarget.from(type));
        }

        /**
         * 创建 selection 构建结果。
         *
         * @return ComputerCollection
         */
        public ComputerCollection buildSelection() {
            return this.build(PredicateBuildTarget.SELECTION);
        }

        /**
         * 创建 existence 构建结果。
         *
         * @return ComputerCollection
         */
        public ComputerCollection buildExistence() {
            return this.build(PredicateBuildTarget.EXISTENCE);
        }

        /**
         * 创建 <code>ComputerCollection</code> .
         *
         * @param target 内部构建目标
         * @return ComputerCollection
         */
        public ComputerCollection build(final PredicateBuildTarget target) {
            Collection<CompiledPredicateResult<?>> results = new ArrayList<CompiledPredicateResult<?>>();
            this.addPredicateResults(this.annotationCollection.getPredicateSpecs(target), results);
            return new ComputerCollection(target, this.annotationCollection, results);
        }

    }

}
