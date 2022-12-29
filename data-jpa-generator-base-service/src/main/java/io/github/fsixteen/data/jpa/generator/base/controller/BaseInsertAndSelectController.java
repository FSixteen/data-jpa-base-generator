package io.github.fsixteen.data.jpa.generator.base.controller;

import java.io.Serializable;

import io.github.fsixteen.data.jpa.generator.base.entities.Entity;
import io.github.fsixteen.data.jpa.generator.base.entities.IdEntity;
import io.github.fsixteen.data.jpa.generator.base.service.BaseInsertAndSelectService;
import io.github.fsixteen.data.jpa.generator.beans.BasePageRequest;

/**
 * @author FSixteen
 * @since V1.0.0
 */
public interface BaseInsertAndSelectController<SI extends BaseInsertAndSelectService<T, ID, I, S>, T extends IdEntity<ID>, ID extends Serializable, I extends Entity, S extends Entity & BasePageRequest>
        extends BaseInsertController<SI, T, ID, I>, BaseSelectController<SI, T, ID, S> {
}