package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.util.Arrays;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SplitNotIn;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;

/**
 * 有关{@link io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SplitNotIn}注解解释器.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
public class SplitNotInBuilderPlugin extends InBuilderPlugin<SplitNotIn> {

    public SplitNotInBuilderPlugin() {
        super(InType.SPLIT_NOT_IN);
    }

    @Override
    Object transition(AnnotationDescriptor<SplitNotIn> ad, Object fieldValue) {
        if (null != fieldValue && String.class.isInstance(fieldValue)) {
            return Arrays.asList(String.class.cast(fieldValue).split(ad.getAnno().decollator()));
        }
        return fieldValue;
    }

}
