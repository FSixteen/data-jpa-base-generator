package io.github.fsixteen.data.jpa.base.generator.service;

import java.io.Serializable;

import io.github.fsixteen.data.jpa.base.generator.entities.Entity;
import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;
import io.github.fsixteen.data.jpa.base.generator.query.BasePageRequest;

/**
 * 通用Service处理类.<br>
 *
 * @author FSixteen
 * @since 1.0.2
 */
public interface BaseUpdateAndSelectService<T extends IdEntity<ID>, ID extends Serializable, U extends IdEntity<ID>, S extends Entity & BasePageRequest>
    extends BaseUpdateService<T, ID, U>, BaseSelectService<T, ID, S> {
}
