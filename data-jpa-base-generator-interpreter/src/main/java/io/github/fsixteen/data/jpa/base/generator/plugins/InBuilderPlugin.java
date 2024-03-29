package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * 有关包含, 不包含某集合类型计算内容的注解解释器.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public class InBuilderPlugin<A extends Annotation> extends AbstractComputerBuilderPlugin<A> {

    private static final Logger log = LoggerFactory.getLogger(InBuilderPlugin.class);

    private ComparableType type = ComparableType.IN;

    public InBuilderPlugin(ComparableType type) {
        this.type = type;
    }

    Object transition(AnnotationDescriptor<A> ad, Object fieldValue) {
        if (null == fieldValue) {
            return null;
        }
        if (fieldValue instanceof Collection<?>) {
            return fieldValue;
        } else if (fieldValue.getClass().isArray()) {
            return Arrays.asList((Object[]) fieldValue);
        } else if (fieldValue instanceof Comparable<?>) {
            return Arrays.asList(fieldValue);
        } else {
            return fieldValue;
        }
    }

    java.util.function.Predicate<Object> getTestPredicate(AnnotationDescriptor<A> ad) throws ReflectiveOperationException {
        return (e) -> true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ComputerDescriptor<A> toPredicate(AnnotationDescriptor<A> ad, Object obj, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb) {
        try {
            Object fieldValue = ad.getValueFieldPd().getReadMethod().invoke(obj);

            if (ad.isRequired() && Objects.isNull(fieldValue)) {
                return this.toNullValuePredicate(ad, root, cb);
            }

            fieldValue = this.transition(ad, fieldValue);

            if (Collection.class.isInstance(fieldValue)) {
                fieldValue = Collection.class.cast(fieldValue).stream().map(ele -> trimIfPresent(ad, ele))
                    .filter(ele -> !(ad.isIgnoreNull() && Objects.isNull(ele)
                        || ad.isIgnoreEmpty() && String.class.isInstance(ele) && String.class.cast(ele).isEmpty()
                        || ad.isIgnoreBlank() && String.class.isInstance(ele) && String.class.cast(ele).trim().isEmpty()))
                    .filter(this.getTestPredicate(ad)).collect(Collectors.toList());
            }

            Optional<Predicate> optional = Optional.ofNullable(fieldValue).filter(it -> List.class.isInstance(it) && !List.class.cast(it).isEmpty())
                // 值类型判断
                .filter(it -> {
                    switch (ad.getValueType()) {
                        case VALUE:
                            return Collection.class.isInstance(it)
                                && root.getModel().getAttribute(ad.getComputerFieldName()).getJavaType() == List.class.cast(it).get(0).getClass();
                        case FUNCTION:
                            return this.checkValueProcessor(ad);
                        default:
                            return false;
                    }
                })
                // 值转换
                .map(it -> {
                    switch (ad.getValueType()) {
                        case VALUE:
                            return cb.literal(Collection.class.cast(it));
                        case FUNCTION:
                            try {
                                return this.<Collection<?>>applyValueProcessor(ad, obj, it, root, query, cb);
                            } catch (ReflectiveOperationException e) {
                                log.error(e.getMessage(), e);
                                return null;
                            }
                        default:
                            return null;
                    }
                })
                // Predicate创建
                .map(it -> {
                    switch (this.type) {
                        case IN:
                        case SPLIT_IN:
                            return root.get(ad.getComputerFieldName()).in(it);
                        default:
                            return root.get(ad.getComputerFieldName()).in(it).not();
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
