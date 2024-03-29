package io.github.fsixteen.data.jpa.base.generator.controller;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * 基础通用Controller.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public interface BaseCommonController {

    @InitBinder
    default void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(new String[] {});
    }

}
