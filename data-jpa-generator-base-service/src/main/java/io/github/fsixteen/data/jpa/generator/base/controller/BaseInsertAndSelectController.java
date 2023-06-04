package io.github.fsixteen.data.jpa.generator.base.controller;

import java.io.Serializable;

import io.github.fsixteen.data.jpa.generator.base.entities.Entity;
import io.github.fsixteen.data.jpa.generator.base.entities.IdEntity;
import io.github.fsixteen.data.jpa.generator.base.query.BasePageRequest;
import io.github.fsixteen.data.jpa.generator.base.service.BaseInsertAndSelectService;

/**
 * 基础添加和查询Controller.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public interface BaseInsertAndSelectController<SI extends BaseInsertAndSelectService<T, ID, I, S>, T extends IdEntity<ID>, ID extends Serializable,
    I extends Entity, S extends Entity & BasePageRequest> extends BaseInsertController<SI, T, ID, I>, BaseSelectController<SI, T, ID, S> {
}
