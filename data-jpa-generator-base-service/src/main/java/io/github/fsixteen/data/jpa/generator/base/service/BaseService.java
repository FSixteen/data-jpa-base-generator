package io.github.fsixteen.data.jpa.generator.base.service;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.generator.base.entities.Entity;
import io.github.fsixteen.data.jpa.generator.base.entities.IdEntity;
import io.github.fsixteen.data.jpa.generator.beans.BasePageRequest;

/**
 * 通用Service处理类.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public interface BaseService<T extends IdEntity<ID>, ID extends Serializable, I extends Entity, D extends IdEntity<ID>, U extends IdEntity<ID>,
    S extends Entity & BasePageRequest> extends BaseInsertAndSelectService<T, ID, I, S>, BaseDeleteService<T, ID, D>, BaseUpdateService<T, ID, U> {

    static final Logger log = LoggerFactory.getLogger(BaseService.class);

}
