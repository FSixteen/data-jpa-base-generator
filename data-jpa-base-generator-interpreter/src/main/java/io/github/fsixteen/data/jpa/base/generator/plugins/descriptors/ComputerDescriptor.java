package io.github.fsixteen.data.jpa.base.generator.plugins.descriptors;

import java.lang.annotation.Annotation;
import java.util.Objects;

import javax.persistence.criteria.Predicate;

import io.github.fsixteen.data.jpa.base.generator.plugins.utils.ArrayUtils;

/**
 * 注解逻辑描述信息.
 *
 * @author FSixteen
 * @since 1.0.0
 */
public final class ComputerDescriptor<A extends Annotation> {

    private AnnotationDescriptor<A> annoDesc;

    private Predicate predicate;

    public static <A extends Annotation> ComputerDescriptor<A> of(final AnnotationDescriptor<A> annoDesc, final Predicate predicate) {
        return new ComputerDescriptor<A>(annoDesc, predicate);
    }

    /**
     * 注解逻辑描述信息构造函数.
     * 
     * @param annoDesc  注解描述信息
     * @param predicate 查询条件
     */
    public ComputerDescriptor(final AnnotationDescriptor<A> annoDesc, final Predicate predicate) {
        super();
        this.annoDesc = annoDesc;
        this.predicate = predicate;
    }

    public AnnotationDescriptor<A> getAnnoDesc() {
        return annoDesc;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public boolean containsScope(final String scope) {
        return Objects.nonNull(this.annoDesc) && ArrayUtils.contains(this.annoDesc.getScope(), scope);
    }

    public boolean isEmpty() {
        return Objects.isNull(annoDesc) && Objects.isNull(predicate);
    }

}
