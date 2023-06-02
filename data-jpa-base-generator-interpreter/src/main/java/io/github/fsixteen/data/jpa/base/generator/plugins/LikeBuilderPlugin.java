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
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * 有关{@link java.lang.String}类型计算内容的相似注解解释器.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class LikeBuilderPlugin extends AbstractComputerBuilderPlugin<Annotation> {

    private static final Logger log = LoggerFactory.getLogger(LikeBuilderPlugin.class);

    private ComparableType type = ComparableType.CONTAINS;

    public LikeBuilderPlugin(ComparableType type) {
        this.type = type;
    }

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
                            return String.class.isInstance(it)
                                && root.getModel().getAttribute(ad.getComputerFieldName()).getJavaType() == ad.getValueField().getType();
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
                            switch (this.type) {
                                case LEFT:
                                case START_WITH:
                                    return cb.<String>literal(String.format("%s%%", String.class.cast(it)));
                                case RIGHT:
                                case END_WITH:
                                    return cb.<String>literal(String.format("%%%s", String.class.cast(it)));
                                case CONTAINS:
                                default:
                                    return cb.<String>literal(String.format("%%%s%%", String.class.cast(it)));
                            }
                        case COLUMN:
                            switch (this.type) {
                                case LEFT:
                                case START_WITH:
                                    return cb.concat(root.<String>get(String.class.cast(it)), "%");
                                case RIGHT:
                                case END_WITH:
                                    return cb.concat("%", root.<String>get(String.class.cast(it)));
                                case CONTAINS:
                                default:
                                    return cb.concat("%", cb.concat(root.<String>get(String.class.cast(it)), "%"));
                            }
                        case FUNCTION:
                            try {
                                switch (this.type) {
                                    case LEFT:
                                    case START_WITH:
                                        return cb.concat(this.<String>applyValueProcessor(ad, obj, root, query, cb), "%");
                                    case RIGHT:
                                    case END_WITH:
                                        return cb.concat("%", this.<String>applyValueProcessor(ad, obj, root, query, cb));
                                    case CONTAINS:
                                    default:
                                        return cb.concat("%", cb.concat(this.<String>applyValueProcessor(ad, obj, root, query, cb), "%"));
                                }
                            } catch (ReflectiveOperationException e) {
                                log.error(e.getMessage(), e);
                                return null;
                            }
                        default:
                            return null;
                    }
                })
                // Predicate创建
                .map(it -> cb.like(root.get(ad.getComputerFieldName()), it));
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
