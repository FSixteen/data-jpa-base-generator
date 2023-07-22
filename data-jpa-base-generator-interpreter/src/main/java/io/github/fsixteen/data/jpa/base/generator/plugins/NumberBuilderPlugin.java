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

import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * 有关{@link java.lang.Number}类型计算内容的注解解释器.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class NumberBuilderPlugin extends AbstractComputerBuilderPlugin<Annotation> {

    private static final Logger log = LoggerFactory.getLogger(NumberBuilderPlugin.class);

    private ComparableType type = ComparableType.GT;

    public NumberBuilderPlugin(ComparableType type) {
        this.type = type;
    }

    @Override
    public ComputerDescriptor<Annotation> toPredicate(AnnotationDescriptor<Annotation> ad, Object obj, Root<?> root, AbstractQuery<?> query,
        CriteriaBuilder cb) {
        try {
            Object fieldValue = ad.getValueFieldPd().getReadMethod().invoke(obj);

            if (ad.isRequired() && Objects.isNull(fieldValue)) {
                return this.toNullValuePredicate(ad, root, cb);
            }

            if (ad.isIgnoreNull() && Objects.isNull(fieldValue)) {
                return null;
            }

            Optional<Predicate> optional = Optional.ofNullable(fieldValue)
                // 值类型判断
                .filter(it -> {
                    switch (ad.getValueType()) {
                        case VALUE:
                            return Number.class.isInstance(it)
                                && root.getModel().getAttribute(ad.getComputerFieldName()).getJavaType() == ad.getValueField().getType();
                        case COLUMN:
                            return String.class.isInstance(it);
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
                            return cb.<Number>literal(Number.class.cast(it));
                        case COLUMN:
                            return root.<Number>get(String.class.cast(it));
                        case FUNCTION:
                            try {
                                return this.<Number>applyValueProcessor(ad, obj, it, root, query, cb);
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
                            return cb.gt(root.get(ad.getComputerFieldName()), it);
                        case GTE:
                            return cb.ge(root.get(ad.getComputerFieldName()), it);
                        case LT:
                            return cb.lt(root.get(ad.getComputerFieldName()), it);
                        case LTE:
                            return cb.le(root.get(ad.getComputerFieldName()), it);
                        default:
                            return cb.equal(root.get(ad.getComputerFieldName()), it);
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
