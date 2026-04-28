package io.github.fsixteen.data.jpa.base.generator.query;

import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 请求实体: 单主键请求.<br>
 *
 * @author FSixteen
 * @since 1.0.1
 */
@Schema(description = "请求实体-单主键请求")
public class LongPrimaryKeyEntity implements IdEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键", requiredMode = RequiredMode.REQUIRED, example = "110")
    private Long id;

    public LongPrimaryKeyEntity() {
    }

    public LongPrimaryKeyEntity(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return LongPrimaryKeyEntity.class.getSimpleName() + " [id=" + id + "]";
    }

}