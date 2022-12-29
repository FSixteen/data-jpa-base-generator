package io.github.fsixteen.data.jpa.generator.base.controller;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * @author FSixteen
 * @since V1.0.0
 */
public interface BaseCommonController {

    @InitBinder
    default void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(new String[] {});
    }

}
