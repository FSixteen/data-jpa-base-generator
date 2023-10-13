package io.github.fsixteen.data.jpa.base.generator.controller;

import java.io.Serializable;

import io.github.fsixteen.data.jpa.base.generator.entities.Entity;
import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;
import io.github.fsixteen.data.jpa.base.generator.query.BasePageRequest;
import io.github.fsixteen.data.jpa.base.generator.service.BaseInsertAndSelectService;

/**
 * 基础添加和查询Controller.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public interface BaseInsertAndSelectController<SI extends BaseInsertAndSelectService<T, ID, I, S>, T extends IdEntity<ID>, ID extends Serializable,
    I extends Entity, S extends Entity & BasePageRequest> extends BaseInsertController<SI, T, ID, I>, BaseSelectController<SI, T, ID, S> {
}
