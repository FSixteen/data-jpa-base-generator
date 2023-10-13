package io.github.fsixteen.data.jpa.base.generator.entities;

import java.io.Serializable;

/**
 * 主键字段接口.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public interface IdEntity<ID extends Serializable> extends Entity {

    /**
     * 获取实体主键.<br>
     * 
     * @return ID
     */
    public ID getId();

    /**
     * 重置实体主键.<br>
     * 
     * @param id 实体主键
     */
    public void setId(ID id);

}
