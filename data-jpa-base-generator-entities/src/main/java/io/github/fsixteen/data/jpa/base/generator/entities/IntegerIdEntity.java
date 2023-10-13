package io.github.fsixteen.data.jpa.base.generator.entities;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 主键字段-Integer类型.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class IntegerIdEntity implements IdEntity<Integer> {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = false, hidden = false, example = "110", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    public Integer id;

    public IntegerIdEntity() {
    }

    public IntegerIdEntity(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

}
