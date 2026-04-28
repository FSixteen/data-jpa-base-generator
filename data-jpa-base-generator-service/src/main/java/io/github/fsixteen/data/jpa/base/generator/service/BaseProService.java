package io.github.fsixteen.data.jpa.base.generator.service;

import java.io.Serializable;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;

/**
 * 通用Service处理类.<br>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public interface BaseProService<T extends IdEntity<ID>, ID extends Serializable> {

    static final Logger log = LoggerFactory.getLogger(BaseProService.class);

    /**
     * 添加/更新处理器.<br>
     *
     * @return Consumer&lt;T&gt;
     */
    default Consumer<T> processor() {
        return (ele) -> {
            if (log.isDebugEnabled()) {
                log.debug("Base Processor Nothing.");
            }
        };
    }

}
