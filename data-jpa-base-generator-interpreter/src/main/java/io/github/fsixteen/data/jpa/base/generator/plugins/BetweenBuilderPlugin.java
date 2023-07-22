package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Between;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * 范围条件.<br>
 * {@link io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Between}注解解释器.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public class BetweenBuilderPlugin extends AbstractComputerBuilderPlugin<Between> {

    private static final Logger log = LoggerFactory.getLogger(BetweenBuilderPlugin.class);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public ComputerDescriptor<Between> toPredicate(AnnotationDescriptor<Between> ad, Object obj, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb) {
        try {
            Object fieldValue = ad.getValueFieldPd().getReadMethod().invoke(obj);

            if (ad.isRequired() && Objects.isNull(fieldValue)) {
                return this.toNullValuePredicate(ad, root, cb);
            }

            if (ad.isIgnoreNull() && Objects.isNull(fieldValue)) {
                return null;
            }

            Optional<Predicate> optional = Optional.ofNullable(fieldValue)
                // 值转换
                .map(it -> Collection.class.isInstance(it) ? Collection.class.cast(it).<Object>toArray(new Object[2]) : it)
                // 值类型判断
                .filter(it -> {
                    switch (ad.getValueType()) {
                        case VALUE:
                            return it.getClass().isArray() && 2 == ((Object[]) it).length;
                        case FUNCTION:
                            return this.checkValueProcessor(ad);
                        default:
                            return false;
                    }
                })
                // Predicate创建
                .map(it -> {
                    switch (ad.getValueType()) {
                        case VALUE:
                            return cb.between(root.get(ad.getComputerFieldName()), ((Comparable[]) it)[0], ((Comparable[]) it)[1]);
                        case FUNCTION:
                            try {
                                Expression<Comparable>[] args = this.applyBiValueProcessor(ad, obj, it, root, query, cb);
                                return cb.between(root.get(ad.getComputerFieldName()), args[0], args[1]);
                            } catch (ReflectiveOperationException e) {
                                log.error(e.getMessage(), e);
                                return null;
                            }
                        default:
                            return null;
                    }
                });
            if (optional.isPresent()) {
                return ComputerDescriptor.of(ad, this.logicReverse(ad, optional.get(), cb));
            } else {
                this.printWarn(ad, root);
            }
        } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
