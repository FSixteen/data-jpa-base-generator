package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.JpaPathCompiler;
import io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProviderRegistry;

/**
 * compiled 子查询公共支撑.
 *
 * <p>
 * 统一处理 select / where 的公共拼装逻辑, 避免 `InTable`、`Exists` 等子查询能力各自维护一套.
 * </p>
 *
 * <p>
 * 该类负责把 {@link CompiledSubquerySpec} 真正展开成 JPA {@link Subquery}：
 * 包括子查询根对象创建、select 路径编译、关联字段约束以及 where 分组递归执行.
 * 因而所有子查询语义最终都只是在外层谓词包裹方式上不同.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledSubquerySupport {

    private CompiledSubquerySupport() {
    }

    /**
     * 在子查询上创建 `from` 根节点并应用 `select` 路径.
     *
     * <p>
     * 这是所有 compiled 子查询的统一入口, 确保 `select` 字段的类型与
     * `Subquery` 泛型保持一致, 避免各个子查询注解各自维护一套根节点和投影装配逻辑.
     * </p>
     *
     * @param spec     已编译的子查询规格
     * @param subQuery 当前待装配的 JPA 子查询
     * @return 子查询的根节点
     */
    public static Root<?> select(final CompiledSubquerySpec spec, final Subquery<?> subQuery) {
        return selectTyped(spec, subQuery);
    }

    /**
     * 在受控泛型上下文中为子查询创建根节点并设置 `select` 投影.
     *
     * <p>
     * 这里显式保留 `Subquery<T>` 泛型, 是为了让 `select` 路径表达式与子查询返回类型保持一致.
     * 这样 `InTable` 一类依赖 `subquery` 元素类型与外层列类型完全对齐的场景,
     * 在 Hibernate 6 的严格类型校验下也能稳定通过.
     * </p>
     *
     * @param spec     已编译的子查询规格
     * @param subQuery 当前待装配的强类型子查询
     * @param <T>      子查询 `select` 元素类型
     * @return 子查询的根节点
     */
    private static <T> Root<?> selectTyped(final CompiledSubquerySpec spec, final Subquery<T> subQuery) {
        Root<?> subRoot = subQuery.from(spec.getFromEntity());
        subQuery.select(pathExpression(subRoot, spec.getSelectPath()));
        return subRoot;
    }

    /**
     * 在 `required` 且字段值为 `null` 时生成“绑定路径 `is null`”短路谓词.
     *
     * <p>
     * 子查询类注解与普通 compare 注解共享相同的 required 语义：
     * 一旦业务要求该参数必须参与过滤, 但运行时值缺失, 则直接退化为对绑定字段本身的
     * `is null` 判定, 而不是继续拼装子查询.
     * </p>
     *
     * @param spec       当前 compiled 注解规格
     * @param fieldValue 当前字段值
     * @param root       外层查询根节点
     * @return required 且字段值为 `null` 时返回短路谓词, 否则返回 {@code null}
     */
    public static Predicate requiredNullPredicate(final CompiledAnnotationSpec<?> spec, final Object fieldValue, final Root<?> root) {
        return spec.getEffectiveOptions().isRequired() && Objects.isNull(fieldValue) ? JpaPathCompiler.compile(root, spec.getBindingPath()).isNull() : null;
    }

    /**
     * 构造一条完整子查询, 包括 `select`、`where` 与可选的关联字段约束.
     *
     * <p>
     * 该方法只负责子查询内部结构的创建, 不负责外层到底使用 `in`、`exists`
     * 还是 `not exists` 包裹. 外层谓词语义由调用方根据 {@link SubqueryMode} 决定.
     * </p>
     *
     * @param spec                    已编译的子查询规格
     * @param outerColumn             外层用于参与关联或 `in` 判断的列
     * @param recursiveAnnotationType 当前递归调用链中的特殊注解类型, 用于阻断自递归
     * @param args                    当前请求参数对象
     * @param outerRoot               外层查询根节点
     * @param query                   当前外层查询对象
     * @param cb                      CriteriaBuilder
     * @return 构造完成的子查询对象
     * @throws IllegalArgumentException 当当前查询对象为空且无法创建子查询时抛出
     */
    public static Subquery<?> createSubquery(final CompiledSubquerySpec spec, final Path<?> outerColumn,
        final Class<? extends Annotation> recursiveAnnotationType, final Object args, final Root<?> outerRoot, final AbstractQuery<?> query,
        final CriteriaBuilder cb) {
        requireQuery(query);
        Subquery<?> subQuery = query.subquery(outerColumn.getJavaType());
        Root<?> subRoot = select(spec, subQuery);
        subQuery.where(resolveWherePredicate(spec, recursiveAnnotationType, args, outerRoot, subRoot, subQuery, cb));
        return subQuery;
    }

    /**
     * 如外层公共选项声明了 `not`, 则对最终子查询谓词执行一次取反.
     *
     * @param spec      当前 compiled 注解规格
     * @param predicate 已生成的子查询谓词
     * @return 根据外层 `negate` 选项处理后的谓词
     */
    public static Predicate applyOuterNegate(final CompiledAnnotationSpec<?> spec, final Predicate predicate) {
        return spec.getEffectiveOptions().isNegate() ? predicate.not() : predicate;
    }

    /**
     * 校验当前执行环境允许创建子查询.
     *
     * @param query 当前查询对象
     * @throws IllegalArgumentException 当查询对象为 {@code null} 时抛出
     */
    public static void requireQuery(final AbstractQuery<?> query) {
        if (Objects.isNull(query)) {
            throw new IllegalArgumentException("Subquery-based predicates require a non-null AbstractQuery");
        }
    }

    /**
     * 根据子查询模式分发到 `in (subquery)` 或 `exists/not exists` 执行路径.
     *
     * @param spec  当前 compiled 注解规格
     * @param args  当前请求参数对象
     * @param root  外层查询根节点
     * @param query 当前查询对象
     * @param cb    CriteriaBuilder
     * @return 生成的子查询谓词；当字段值被忽略或模式不满足触发条件时返回 {@code null}
     */
    public static Predicate create(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb) {
        CompiledSubquerySpec subquerySpec = CompiledSpecializedSpecs.subquery(spec);
        SubqueryMode mode = subquerySpec.getMode();
        if (SubqueryMode.IN == mode) {
            return createInTable(spec, subquerySpec, args, root, query, cb);
        }
        return createExists(spec, subquerySpec, args, root, query, cb, SubqueryMode.NOT_EXISTS == mode);
    }

    /**
     * 执行 `in (subquery)` 语义.
     *
     * @param spec         当前 compiled 注解规格
     * @param subquerySpec 已编译的子查询规格
     * @param args         当前请求参数对象
     * @param root         外层查询根节点
     * @param query        当前查询对象
     * @param cb           CriteriaBuilder
     * @return 生成的 `in (subquery)` 谓词；当字段值被忽略时返回 {@code null}
     */
    private static Predicate createInTable(final CompiledAnnotationSpec<? extends Annotation> spec, final CompiledSubquerySpec subquerySpec, final Object args,
        final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) {
        Object fieldValue = spec.readAndTrim(args);
        Predicate nullPredicate = requiredNullPredicate(spec, fieldValue, root);
        if (Objects.nonNull(nullPredicate)) {
            return nullPredicate;
        }
        if (spec.shouldIgnore(fieldValue)) {
            return null;
        }
        return createTypedInTable(spec, subquerySpec, args, root, query, cb);
    }

    /**
     * 在受控泛型上下文中执行 `in (subquery)` 语义.
     *
     * <p>
     * 该方法会先把外层源路径编译为 {@link Path}{@code <T>}, 再创建返回同一类型
     * 的 {@link Subquery}{@code <T>}, 最终生成 `outerColumn.in(subQuery)`.
     * 这样可以确保 `c1 in (subquery)` 两侧元素类型严格一致, 避免 Spring Boot 3 /
     * Hibernate 6 下因右侧被推断成过宽类型而触发比较异常.
     * </p>
     *
     * @param spec         当前 compiled 注解规格
     * @param subquerySpec 已编译的子查询规格
     * @param args         当前请求参数对象
     * @param root         外层查询根节点
     * @param query        当前查询对象
     * @param cb           CriteriaBuilder
     * @param <T>          `IN` 比较元素类型
     * @return 生成的 `in (subquery)` 谓词
     */
    @SuppressWarnings("unchecked")
    private static <T> Predicate createTypedInTable(final CompiledAnnotationSpec<? extends Annotation> spec, final CompiledSubquerySpec subquerySpec,
        final Object args, final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb) {
        Path<T> outerColumn = (Path<T>) JpaPathCompiler.compile(root, subquerySpec.getSourcePath());
        Subquery<T> subQuery = castSubquery(createSubquery(subquerySpec, outerColumn, spec.getAnnotationType(), args, root, query, cb));
        return applyOuterNegate(spec, outerColumn.in(subQuery));
    }

    /**
     * 执行 `exists / not exists` 语义.
     *
     * <p>
     * 当前实现要求绑定字段值显式为 `true` 才真正触发子查询.
     * 这样 `Exists` 一类注解可被视为布尔开关, 而不是无条件参与过滤.
     * </p>
     *
     * @param spec         当前 compiled 注解规格
     * @param subquerySpec 已编译的子查询规格
     * @param args         当前请求参数对象
     * @param root         外层查询根节点
     * @param query        当前查询对象
     * @param cb           CriteriaBuilder
     * @param negate       是否先对 `exists` 结果做一次内部取反
     * @return 生成的 `exists/not exists` 谓词；当开关值不是 `true` 或字段值应被忽略时返回 {@code null}
     */
    private static Predicate createExists(final CompiledAnnotationSpec<? extends Annotation> spec, final CompiledSubquerySpec subquerySpec, final Object args,
        final Root<?> root, final AbstractQuery<?> query, final CriteriaBuilder cb, final boolean negate) {
        Object fieldValue = spec.readAndTrim(args);
        Predicate nullPredicate = requiredNullPredicate(spec, fieldValue, root);
        if (Objects.nonNull(nullPredicate)) {
            return nullPredicate;
        }
        if (!Objects.equals(Boolean.TRUE, fieldValue) || spec.shouldIgnore(fieldValue) || Objects.isNull(query)) {
            return null;
        }
        Path<?> outerColumn = JpaPathCompiler.compile(root, subquerySpec.getSourcePath());
        Subquery<?> subQuery = createSubquery(subquerySpec, outerColumn, spec.getAnnotationType(), args, root, query, cb);
        Predicate predicate = cb.exists(subQuery);
        if (negate) {
            predicate = predicate.not();
        }
        return applyOuterNegate(spec, predicate);
    }

    /**
     * 组装子查询 `where` 条件, 包括关联约束、嵌套叶子与 group 树.
     *
     * @param spec                    已编译的子查询规格
     * @param recursiveAnnotationType 当前递归调用链中的特殊注解类型
     * @param args                    当前请求参数对象
     * @param outerRoot               外层查询根节点
     * @param subRoot                 子查询根节点
     * @param subQuery                当前子查询对象
     * @param cb                      CriteriaBuilder
     * @return 合并后的 `where` 谓词；若没有任何有效条件则返回 {@code null}
     */
    public static Predicate resolveWherePredicate(final CompiledSubquerySpec spec, final Class<? extends Annotation> recursiveAnnotationType, final Object args,
        final Root<?> outerRoot, final Root<?> subRoot, final AbstractQuery<?> subQuery, final CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        if (Objects.nonNull(spec.getCorrelatedPath()) && !spec.getCorrelatedPath().isEmpty()) {
            predicates.add(cb.equal(JpaPathCompiler.compile(subRoot, spec.getCorrelatedPath()), JpaPathCompiler.compile(outerRoot, spec.getSourcePath())));
        }
        Predicate nestedPredicate = resolveNestedPredicate(spec, args, subRoot, subQuery, cb);
        if (Objects.nonNull(nestedPredicate)) {
            predicates.add(nestedPredicate);
        }
        Predicate groupPredicate = resolveGroupPredicate(spec, recursiveAnnotationType, args, subRoot, subQuery, cb);
        if (Objects.nonNull(groupPredicate)) {
            predicates.add(groupPredicate);
        }
        if (predicates.isEmpty()) {
            return null;
        }
        if (1 == predicates.size()) {
            return predicates.get(0);
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    /**
     * 递归执行子查询内部的 group 树.
     *
     * @param spec                    已编译的子查询规格
     * @param recursiveAnnotationType 当前递归调用链中的特殊注解类型
     * @param args                    当前请求参数对象
     * @param subRoot                 子查询根节点
     * @param subQuery                当前子查询对象
     * @param cb                      CriteriaBuilder
     * @return group 谓词；若当前不应从 group 解析则返回 {@code null}
     */
    private static Predicate resolveGroupPredicate(final CompiledSubquerySpec spec, final Class<? extends Annotation> recursiveAnnotationType,
        final Object args, final Root<?> subRoot, final AbstractQuery<?> subQuery, final CriteriaBuilder cb) {
        if (shouldResolveFromGroup(spec, recursiveAnnotationType)) {
            return CompiledPredicateGroupResolver.create(spec.getPredicateGroupSpec(), args, subRoot, subQuery, cb);
        }
        return null;
    }

    /**
     * 执行子查询上的单个嵌套叶子注解.
     *
     * @param spec     已编译的子查询规格
     * @param args     当前请求参数对象
     * @param subRoot  子查询根节点
     * @param subQuery 当前子查询对象
     * @param cb       CriteriaBuilder
     * @return 嵌套叶子谓词；未声明嵌套叶子时返回 {@code null}
     */
    private static Predicate resolveNestedPredicate(final CompiledSubquerySpec spec, final Object args, final Root<?> subRoot, final AbstractQuery<?> subQuery,
        final CriteriaBuilder cb) {
        if (Objects.nonNull(spec.getNestedPredicateSpec())) {
            return CompiledPredicateProviderRegistry.require(spec.getNestedPredicateSpec().getAnnotationType()).create(spec.getNestedPredicateSpec(), args,
                subRoot, subQuery, cb);
        }
        return null;
    }

    /**
     * 防止子查询分组树再次回到当前特殊注解自身, 造成递归.
     *
     * @param spec                    已编译的子查询规格
     * @param recursiveAnnotationType 当前递归调用链中的特殊注解类型
     * @return 当 group 树允许继续递归解析时返回 {@code true}
     */
    private static boolean shouldResolveFromGroup(final CompiledSubquerySpec spec, final Class<? extends Annotation> recursiveAnnotationType) {
        PredicateGroupSpec groupSpec = spec.getPredicateGroupSpec();
        if (Objects.isNull(groupSpec) || groupSpec.isEmpty()) {
            return false;
        }
        if (!groupSpec.getGroups().isEmpty()) {
            return true;
        }
        for (CompiledAnnotationSpec<?> annotation : groupSpec.getAnnotations()) {
            if (recursiveAnnotationType == annotation.getAnnotationType()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将点路径编译结果收窄为与 `Subquery<T>` 对齐的 `Expression<T>`.
     *
     * @param root 子查询根节点
     * @param path `select` 路径
     * @param <T>  目标投影类型
     * @return 与子查询泛型对齐的路径表达式
     */
    @SuppressWarnings("unchecked")
    private static <T> Expression<T> pathExpression(final Root<?> root, final String path) {
        // Subquery.select 需要与 Subquery<T> 对齐的精确 Expression<T>；点路径编译后只能在这里做一次受控收窄.
        return (Expression<T>) JpaPathCompiler.compileExpression(root, path);
    }

    /**
     * 将无界子查询引用桥接为调用方当前需要的强类型子查询引用.
     *
     * @param subQuery 待桥接的子查询
     * @param <T>      调用方期望的子查询元素类型
     * @return 收窄后的强类型子查询引用
     */
    @SuppressWarnings("unchecked")
    private static <T> Subquery<T> castSubquery(final Subquery<?> subQuery) {
        return (Subquery<T>) subQuery;
    }

}
