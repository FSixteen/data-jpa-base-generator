package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;

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
 * 有关{@link java.lang.Comparable}类型计算内容的注解解释器.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
public class ComparableBuilderPlugin extends AbstractComputerBuilderPlugin<Annotation> {

    private static final Logger log = LoggerFactory.getLogger(ComparableBuilderPlugin.class);

    /**
     * {@link ComparableBuilderPlugin}执行方式.<br>
     * 
     * @author FSixteen
     * @since V1.0.0
     */
    public static enum ComparableType {
        GT, GTE, LT, LTE, EQ
    }

    private ComparableType type = ComparableType.GT;

    public ComparableBuilderPlugin(ComparableType type) {
        this.type = type;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public ComputerDescriptor<Annotation> toPredicate(AnnotationDescriptor<Annotation> ad, Object obj, Root<?> root, AbstractQuery<?> query,
            CriteriaBuilder cb) {
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

            Optional<Predicate> optional = Optional.ofNullable(fieldValue)
                    // 值类型判断
                    .filter(it -> {
                        switch (ad.getValueType()) {
                            case VALUE:
                                return Comparable.class.isInstance(it) && ad.getComputerField().getType() == ad.getValueField().getType();
                            case COLUMN:
                                return String.class.isInstance(it);
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
                                return cb.<Comparable<?>>literal(Comparable.class.cast(it));
                            case COLUMN:
                                return root.<Comparable<?>>get(String.class.cast(it));
                            case FUNCTION:
                                try {
                                    return this.<Comparable<?>>applyValueProcessor(ad, obj, root, query, cb);
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
                            case GT:
                                return cb.<Comparable>greaterThan(root.get(ad.getComputerFieldName()), it);
                            case GTE:
                                return cb.<Comparable>greaterThanOrEqualTo(root.get(ad.getComputerFieldName()), it);
                            case LT:
                                return cb.<Comparable>lessThan(root.get(ad.getComputerFieldName()), it);
                            case LTE:
                                return cb.<Comparable>lessThanOrEqualTo(root.get(ad.getComputerFieldName()), it);
                            case EQ:
                            default:
                                return cb.equal(root.get(ad.getComputerFieldName()), it);
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
