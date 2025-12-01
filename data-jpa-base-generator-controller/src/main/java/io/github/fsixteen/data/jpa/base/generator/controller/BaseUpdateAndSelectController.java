package io.github.fsixteen.data.jpa.base.generator.controller;

import java.io.Serializable;

import io.github.fsixteen.data.jpa.base.generator.entities.Entity;
import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;
import io.github.fsixteen.data.jpa.base.generator.query.BasePageRequest;
import io.github.fsixteen.data.jpa.base.generator.service.BaseUpdateAndSelectService;

/**
 * 基础更新和查询Controller.<br>
 * 
 * @author FSixteen
 * @since 1.0.2
 */
public interface BaseUpdateAndSelectController<SI extends BaseUpdateAndSelectService<T, ID, U, S>, T extends IdEntity<ID>, ID extends Serializable,
    U extends IdEntity<ID>, S extends Entity & BasePageRequest> extends BaseUpdateController<SI, T, ID, U>, BaseSelectController<SI, T, ID, S> {
}
