package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ValueInType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.InTable;
import io.github.fsixteen.data.jpa.base.generator.plugins.cache.PluginsCache;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * 夸表包含条件.<br>
 * {@link io.github.fsixteen.data.jpa.base.generator.annotations.plugins.InTable}注解解释器.<br>
 * 
 * @author FSixteen
 * @since V1.0.0
 */
public class InTableBuilderPlugin extends AbstractComputerBuilderPlugin<InTable> {

    private static final Logger log = LoggerFactory.getLogger(InTableBuilderPlugin.class);

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ComputerDescriptor<InTable> toPredicate(AnnotationDescriptor<InTable> ad, Object obj, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb) {
        try {
            Object fieldValue = this.trimIfPresent(ad, ad.getValueFieldPd().getReadMethod().invoke(obj));

            if (ad.isRequired() && Objects.isNull(fieldValue)) {
                return this.toNullValuePredicate(ad, root, cb);
            }

            if (ad.isIgnoreNull() && Objects.isNull(fieldValue)) {
                return null;
            }

            if (ad.isIgnoreEmpty() && String.class.isInstance(fieldValue) && String.class.cast(fieldValue).isEmpty()) {
                return null;
            }

            if (ad.isIgnoreBlank() && String.class.isInstance(fieldValue) && String.class.cast(fieldValue).trim().isEmpty()) {
                return null;
            }

            InTable anno = ad.getAnno();
            Subquery<?> subQuery = query.subquery(anno.targetEntity());
            Root<?> subRoot = subQuery.from(anno.targetEntity());
            subQuery.select(subRoot.get(anno.referencedColumnName()));
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(root.get(anno.columnName()).in(subQuery));
            Class<Annotation> annoClass = anno.valueInProcessorClass();
            if (ValueInType.TARGET == anno.valueInType()) {
                ComputerDescriptor<?> cd = PluginsCache.reference(annoClass).toPredicate((AnnotationDescriptor) ad, obj, subRoot, subQuery, cb);
                subQuery.where(cd.getPredicate());
            } else {
                ComputerDescriptor<?> cd = PluginsCache.reference(annoClass).toPredicate((AnnotationDescriptor) ad, obj, root, query, cb);
                predicates.add(cd.getPredicate());
            }
            Predicate predicate = cb.and(predicates.toArray(new Predicate[predicates.size()]));
            return ComputerDescriptor.of(ad, this.logicReverse(ad, predicate, cb));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
