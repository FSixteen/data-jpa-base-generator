package io.github.fsixteen.data.jpa.generator.base.controller;

import java.io.Serializable;

import io.github.fsixteen.data.jpa.generator.base.entities.Entity;
import io.github.fsixteen.data.jpa.generator.base.entities.IdEntity;
import io.github.fsixteen.data.jpa.generator.base.service.BaseService;
import io.github.fsixteen.data.jpa.generator.beans.BasePageRequest;

/**
 * @author FSixteen
 * @since V1.0.0
 */
public interface BaseController<SI extends BaseService<T, ID, I, D, U, S>, T extends IdEntity<ID>, ID extends Serializable, I extends Entity, D extends IdEntity<ID>, U extends IdEntity<ID>, S extends Entity & BasePageRequest>
        extends BaseInsertAndSelectController<SI, T, ID, I, S>, BaseDeleteController<SI, T, ID, D>, BaseUpdateController<SI, T, ID, U> {

    @Override
    public abstract SI getService();

}
