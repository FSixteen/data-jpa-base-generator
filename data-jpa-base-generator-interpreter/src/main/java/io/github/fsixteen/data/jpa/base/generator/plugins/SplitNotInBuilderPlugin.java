package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SplitNotIn;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;

/**
 * 有关{@link io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SplitNotIn}注解解释器.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class SplitNotInBuilderPlugin extends InBuilderPlugin<SplitNotIn> {

    public SplitNotInBuilderPlugin() {
        super(ComparableType.SPLIT_NOT_IN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Collection<?> transition(AnnotationDescriptor<SplitNotIn> ad, Object fieldValue) {
        if (null != fieldValue && String.class.isInstance(fieldValue)) {
            return Stream.of(String.class.cast(fieldValue).split(ad.getAnno().decollator())).map(e -> {
                switch (ad.getAnno().targetType()) {
                    case TO_DATE:
                    case TO_LD:
                    case TO_LT:
                    case TO_LDT:
                        return ad.getAnno().targetType().parse(e, ad.getAnno().targetFormat());
                    default:
                        return ad.getAnno().targetType().parse(e);
                }
            }).collect(Collectors.toList());
        }
        return super.transition(ad, fieldValue);
    }

}
