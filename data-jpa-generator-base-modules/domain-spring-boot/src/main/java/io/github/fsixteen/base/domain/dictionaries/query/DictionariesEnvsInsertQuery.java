package io.github.fsixteen.base.domain.dictionaries.query;

import javax.validation.constraints.NotNull;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Unique;
import io.github.fsixteen.data.jpa.generator.base.entities.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 请求实体: 添加请求: 系统字典信息-系统字典环境编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@ApiModel(value = "请求添加实体-系统字典信息-系统字典环境编码")
@Schema(description = "请求添加实体-系统字典信息-系统字典环境编码")
public class DictionariesEnvsInsertQuery implements Entity {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "* 请指定环境编码")
    @Schema(description = "环境编码", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "DEVEL", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "环境编码", required = true, hidden = false, example = "DEVEL", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Unique
    private String code;

    @NotNull(message = "* 请指定环境描述")
    @Schema(description = "环境描述", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "开发环境", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "环境描述", required = true, hidden = false, example = "开发环境", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    private String description;

    @NotNull(message = "* 请指定启用状态")
    @Schema(description = "启用状态", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "false", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "启用状态", required = true, hidden = false, example = "false", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    private Boolean enabled = Boolean.FALSE;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return String.format("DictionariesEnvsInsertQuery [code=%s, description=%s, enabled=%s]", code, description, enabled);
    }

}
