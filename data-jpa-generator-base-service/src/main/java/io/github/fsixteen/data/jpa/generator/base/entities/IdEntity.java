package io.github.fsixteen.data.jpa.generator.base.entities;

import java.io.Serializable;

/**
 * 主键字段-持久层端.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
public interface IdEntity<ID extends Serializable> extends Entity {

    public ID getId();

    public void setId(ID id);

}
