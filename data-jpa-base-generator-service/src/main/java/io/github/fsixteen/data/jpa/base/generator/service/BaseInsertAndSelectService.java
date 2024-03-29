package io.github.fsixteen.data.jpa.base.generator.service;

import java.io.Serializable;

import io.github.fsixteen.data.jpa.base.generator.entities.Entity;
import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;
import io.github.fsixteen.data.jpa.base.generator.query.BasePageRequest;

/**
 * 通用Service处理类.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public interface BaseInsertAndSelectService<T extends IdEntity<ID>, ID extends Serializable, I extends Entity, S extends Entity & BasePageRequest>
    extends BaseInsertService<T, ID, I>, BaseSelectService<T, ID, S> {
}
