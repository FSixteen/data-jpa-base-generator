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

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ValueInType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.InTable;
import io.github.fsixteen.data.jpa.base.generator.plugins.cache.PluginsCache;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * 跨表包含条件.<br>
 * {@link io.github.fsixteen.data.jpa.base.generator.annotations.plugins.InTable}注解解释器.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public class InTableBuilderPlugin extends AbstractComputerBuilderPlugin<InTable> {

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ComputerDescriptor<InTable> toPredicate(AnnotationDescriptor<InTable> ad, Object obj, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb)
        throws ClassNotFoundException {
        try {
            Object fieldValue = this.trimIfPresent(ad, ad.getValueFieldPd().getReadMethod().invoke(obj));

            if (ad.isRequired() && Objects.isNull(fieldValue)) {
                return this.toNullValuePredicate(ad, root);
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
            List<Predicate> predicates = new ArrayList<>();

            BuilderPlugin<? extends Annotation> plugin = null;
            try {
                if (null == plugin && Void.class != anno.valueInProcessorClass()) {
                    plugin = PluginsCache.reference(anno.valueInProcessorClass().getName());
                }
                if (null == plugin && !"".equals(anno.valueInProcessorClassName())) {
                    plugin = PluginsCache.reference(anno.valueInProcessorClassName());
                }
                if (null == plugin) {
                    plugin = PluginsCache.reference(Equal.class);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                plugin = PluginsCache.reference(Equal.class);
            }

            if (ValueInType.TARGET == anno.valueInType()) {
                Subquery<?> subQuery = query.subquery(anno.targetEntity());
                Root<?> subRoot = subQuery.from(anno.targetEntity());
                subQuery.select(subRoot.get(anno.referencedColumnName()));
                subQuery.where(plugin.toPredicate((AnnotationDescriptor) ad, obj, subRoot, subQuery, cb).getPredicate());
                predicates.add(root.get(anno.columnName()).in(subQuery));
            } else {
                ComputerDescriptor<?> cd = plugin.toPredicate((AnnotationDescriptor) ad, obj, root, query, cb);
                predicates.add(cd.getPredicate());
            }
            Predicate predicate = cb.and(predicates.toArray(new Predicate[predicates.size()]));
            return ComputerDescriptor.of(ad, this.logicReverse(ad.isNot(), predicate));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

}
