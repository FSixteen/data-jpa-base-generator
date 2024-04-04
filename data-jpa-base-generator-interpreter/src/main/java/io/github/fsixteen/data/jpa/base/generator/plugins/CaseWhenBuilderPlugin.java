package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;

/**
 * TODO :: 规划中.
 * 分支条件(select * from table_name where (xxxxxx)).<br>
 *
 * @author FSixteen
 * @since 1.0.1
 */
public class CaseWhenBuilderPlugin<A extends Annotation> extends AbstractComputerBuilderPlugin<A> {
    private static final Logger LOG = LoggerFactory.getLogger(ComparableBuilderPlugin.class);

    @Override
    public ComputerDescriptor<A> toPredicate(AnnotationDescriptor<A> ad, Object obj, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb) {
        // TODO 补充实现
        return null;
    }

}
