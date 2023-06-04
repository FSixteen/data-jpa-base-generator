package io.github.fsixteen.base.domain.dictionaries.query;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal;
import io.github.fsixteen.data.jpa.generator.base.query.DefaultPageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 请求实体: 查询请求: 系统字典信息-系统字典环境编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@ApiModel(value = "请求查询实体-系统字典信息-系统字典环境编码")
@Schema(description = "请求查询实体-系统字典信息-系统字典环境编码")
public class DictionariesEnvsSelectQuery extends DefaultPageRequest {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = false, hidden = false, example = "110", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Equal
    private Integer id;

    @Schema(description = "环境编码", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "DEVEL", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "环境编码", required = false, hidden = false, example = "DEVEL", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Equal
    private String code;

    @Schema(description = "启用状态", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "false", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "启用状态", required = false, hidden = false, example = "false", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Equal
    private Boolean enabled;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return DictionariesEnvsSelectQuery.class.getSimpleName() + " [id=" + id + ", code=" + code + ", enabled=" + enabled + ", getPage()=" + getPage()
            + ", getSize()=" + getSize() + "]";
    }
}
