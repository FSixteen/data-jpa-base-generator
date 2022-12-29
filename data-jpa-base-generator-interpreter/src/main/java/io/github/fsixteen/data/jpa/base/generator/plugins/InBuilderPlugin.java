package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.DefaultValueProcessor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * @author FSixteen
 * @since V1.0.0
 */
public class InBuilderPlugin<AN extends Annotation> extends ComputerBuilderPlugin<AN> {
    private static final Logger log = LoggerFactory.getLogger(InBuilderPlugin.class);

    public static enum InType {
        IN, NOT_IN, SPLIT_IN, SPLIT_NOT_IN
    }

    private InType type = InType.IN;

    public InBuilderPlugin(InType type) {
        this.type = type;
    }

    Object transition(AnnotationDescriptor<AN> ad, Object fieldValue) {
        if (null != fieldValue && fieldValue.getClass().isArray()) {
            return Arrays.asList((Object[]) fieldValue);
        }
        return fieldValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ComputerDescriptor<AN> toPredicate(AnnotationDescriptor<AN> ad, Object obj, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb) {
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
                        .collect(Collectors.toList());
            }

            Optional<Predicate> optional = Optional.ofNullable(fieldValue)
                    // 值类型判断
                    .filter(it -> {
                        switch (ad.getValueType()) {
                            case VALUE:
                                return Collection.class.isInstance(it) && ad.getComputerField().getType() == ad.getValueField().getType();
                            case FUNCTION:
                                return Objects.nonNull(ad.getValueProcessor()) && DefaultValueProcessor.class != ad.getValueProcessor().processorClass();
                            default:
                                return false;
                        }
                    })
                    // 值转换
                    .map(it -> {
                        switch (ad.getValueType()) {
                            case VALUE:
                                return cb.literal(Collection.class.isInstance(it));
                            case FUNCTION:
                                try {
                                    return this.<Collection<?>>applyValueProcessor(ad, obj, root, query, cb);
                                } catch (ReflectiveOperationException e) {
                                    log.error(e.getMessage(), e);
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
                this.printWarn(ad);
            }
        } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
