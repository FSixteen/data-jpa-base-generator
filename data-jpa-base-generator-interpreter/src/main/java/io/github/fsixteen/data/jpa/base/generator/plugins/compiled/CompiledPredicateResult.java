package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;
import java.util.Objects;

import javax.persistence.criteria.Predicate;

/**
 * 单个注解在运行期的执行结果.
 *
 * <p>
 * 该对象把“注解的 compiled 规格”和“最终生成的 JPA {@link Predicate}”绑定在一起,
 * 供后续 scope 过滤、group 组装和调试测试继续访问原始规格语义.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledPredicateResult<A extends Annotation> {

    /**
     * 这里把“编译后的注解规格”和“最终生成的 Predicate”绑在一起,
     * 是为了后续分组、scope 过滤仍然可以基于注解元数据工作, 而不需要回头再查原对象.
     */
    private final CompiledAnnotationSpec<A> spec;

    private final Predicate predicate;

    private CompiledPredicateResult(final CompiledAnnotationSpec<A> spec, final Predicate predicate) {
        this.spec = spec;
        this.predicate = predicate;
    }

    /**
     * 绑定 compiled 规格与最终生成的谓词.
     */
    public static <A extends Annotation> CompiledPredicateResult<A> of(final CompiledAnnotationSpec<A> spec, final Predicate predicate) {
        return new CompiledPredicateResult<A>(spec, predicate);
    }

    /**
     * 返回当前结果对应的 compiled 注解规格.
     */
    public CompiledAnnotationSpec<A> getSpec() {
        return this.spec;
    }

    /**
     * 返回当前结果中的 JPA 谓词.
     */
    public Predicate getPredicate() {
        return this.predicate;
    }

    /**
     * 判断当前结果是否同时缺少规格与谓词.
     */
    public boolean isEmpty() {
        return Objects.isNull(this.spec) && Objects.isNull(this.predicate);
    }

}
