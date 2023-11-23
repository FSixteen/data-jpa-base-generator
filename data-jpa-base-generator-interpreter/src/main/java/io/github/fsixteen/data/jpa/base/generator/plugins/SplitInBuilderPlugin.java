package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.util.Arrays;
import java.util.Collection;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SplitIn;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;

/**
 * 有关{@link io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SplitIn}注解解释器.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class SplitInBuilderPlugin extends InBuilderPlugin<SplitIn> {

    public SplitInBuilderPlugin() {
        super(ComparableType.SPLIT_IN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Collection<?> transition(AnnotationDescriptor<SplitIn> ad, Object fieldValue) {
        if (null != fieldValue && String.class.isInstance(fieldValue)) {
            return Arrays.asList(String.class.cast(fieldValue).split(ad.getAnno().decollator()));
        }
        return super.transition(ad, fieldValue);
    }

}
