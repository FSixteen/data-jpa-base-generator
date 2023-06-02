package io.github.fsixteen.data.jpa.generator.base.entities;

import java.io.Serializable;

/**
 * 主键字段接口.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public interface IdEntity<ID extends Serializable> extends Entity {

    public ID getId();

    public void setId(ID id);

}
