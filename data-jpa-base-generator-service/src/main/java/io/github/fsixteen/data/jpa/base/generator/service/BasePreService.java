package io.github.fsixteen.data.jpa.base.generator.service;

import java.io.Serializable;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.entities.Entity;
import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;

/**
 * 通用前置Service处理类.<br>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public interface BasePreService<T extends IdEntity<ID>, ID extends Serializable> {

    static final Logger log = LoggerFactory.getLogger(BasePreService.class);

    /**
     * 添加/更新前置处理器.<br>
     * 
     * @return Consumer&lt;I&gt;
     */
    default Consumer<Entity> preprocessor() {
        return (args) -> {
            if (log.isDebugEnabled()) {
                log.debug("Base Pre Processor Nothing.");
            }
        };
    }

}
