package io.github.fsixteen.data.jpa.generator.base.entities;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 主键字段-Long类型.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class LongIdEntity implements IdEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = false, hidden = false, example = "110", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    public Long id;

    public LongIdEntity() {
    }

    public LongIdEntity(Long id) {
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

}
