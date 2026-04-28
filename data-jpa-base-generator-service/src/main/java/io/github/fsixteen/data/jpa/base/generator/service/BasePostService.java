package io.github.fsixteen.data.jpa.base.generator.service;

import java.io.Serializable;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.entities.Entity;
import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;

/**
 * 通用后置Service处理类.<br>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public interface BasePostService<T extends IdEntity<ID>, ID extends Serializable> {

    static final Logger log = LoggerFactory.getLogger(BasePostService.class);

    /**
     * 添加/更新后置处理器.<br>
     *
     * @return BiConsumer&lt;T, I&gt;
     */
    default BiConsumer<T, Entity> postprocessor() {
        return (ele, args) -> {
            if (log.isDebugEnabled()) {
                log.debug("Base Post Processor Nothing.");
            }
        };
    }

    /**
     * 添加/更新后置处理器.<br>
     *
     * @return BiConsumer&lt;Iterable&lt;T&gt, Iterable&lt;A&gt&gt;
     */
    default BiConsumer<Iterable<T>, Iterable<? extends Entity>> allPostprocessor() {
        return (ele, args) -> {
            if (log.isDebugEnabled()) {
                log.debug("Base Post Processor Nothing.");
            }
        };
    }

}
