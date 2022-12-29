package io.github.fsixteen.data.jpa.base.generator.plugins.descriptors;

import java.lang.annotation.Annotation;
import java.util.Objects;

import javax.persistence.criteria.Predicate;

/**
 * 注解逻辑描述信息.
 *
 * @author FSixteen
 * @since V1.0.0
 */
public final class ComputerDescriptor<AN extends Annotation> {

    private AnnotationDescriptor<AN> annoDesc;

    private Predicate predicate;

    public static <AN extends Annotation> ComputerDescriptor<AN> of(final AnnotationDescriptor<AN> annoDesc, final Predicate predicate) {
        return new ComputerDescriptor<AN>(annoDesc, predicate);
    }

    public ComputerDescriptor(final AnnotationDescriptor<AN> annoDesc, final Predicate predicate) {
        super();
        this.annoDesc = annoDesc;
        this.predicate = predicate;
    }

    public AnnotationDescriptor<AN> getAnnoDesc() {
        return annoDesc;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public boolean isEmpty() {
        return Objects.isNull(annoDesc) && Objects.isNull(predicate);
    }

}
