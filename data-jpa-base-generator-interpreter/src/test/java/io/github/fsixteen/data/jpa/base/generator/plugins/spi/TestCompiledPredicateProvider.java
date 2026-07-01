package io.github.fsixteen.data.jpa.base.generator.plugins.spi;

import java.lang.annotation.Annotation;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.CompiledAnnotationSpec;

public class TestCompiledPredicateProvider implements CompiledPredicateProvider {

    @Override
    public Class<? extends Annotation> annotationType() {
        return SpiCompiledSelectable.class;
    }

    @Override
    public Predicate create(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb) {
        return cb.equal(root.get(spec.getBindingPath()), root.get("audit").get("currentStatus"));
    }

}
