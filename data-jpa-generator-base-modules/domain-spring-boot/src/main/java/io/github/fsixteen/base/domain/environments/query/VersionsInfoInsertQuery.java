package io.github.fsixteen.base.domain.environments.query;

import javax.validation.constraints.NotNull;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Unique;
import io.github.fsixteen.data.jpa.generator.base.entities.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 请求实体: 添加请求: 系统配置信息-模块版本信息.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@ApiModel(value = "请求添加实体-系统配置信息-模块版本信息")
@Schema(description = "请求添加实体-系统配置信息-模块版本信息")
public class VersionsInfoInsertQuery implements Entity {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "* 请指定模块编码")
    @Schema(description = "模块编码", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "FSN_VERSIONS", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "模块编码", required = true, hidden = false, example = "FSN_VERSIONS",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Unique
    private String code;

    @NotNull(message = "* 请指定模块描述")
    @Schema(description = "模块描述", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "模块版本信息", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "模块描述", required = true, hidden = false, example = "模块版本信息", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    private String description;

    @Schema(description = "模块上一版本", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "1.0.0", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "模块上一版本", required = true, hidden = false, example = "1.0.0",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    private String preVersion;

    @NotNull(message = "* 请指定模块版本")
    @Schema(description = "模块版本", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "1.0.1", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "模块版本", required = true, hidden = false, example = "1.0.1", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Unique
    private String currVersion;

    @Schema(description = "模块下一版本", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "1.0.2", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "模块下一版本", required = false, hidden = false, example = "1.0.2",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    private String postVersion;

    @NotNull(message = "* 请指定模块发布日期")
    @Schema(description = "模块发布日期", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "1685090852009",
        accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "模块发布日期", required = true, hidden = false, example = "1685090852009",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    private Long versionDate;

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

    public String getPreVersion() {
        return preVersion;
    }

    public void setPreVersion(String preVersion) {
        this.preVersion = preVersion;
    }

    public String getCurrVersion() {
        return currVersion;
    }

    public void setCurrVersion(String currVersion) {
        this.currVersion = currVersion;
    }

    public String getPostVersion() {
        return postVersion;
    }

    public void setPostVersion(String postVersion) {
        this.postVersion = postVersion;
    }

    public Long getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(Long versionDate) {
        this.versionDate = versionDate;
    }

    @Override
    public String toString() {
        return "VersionsInfoInsertQuery [code=" + code + ", description=" + description + ", preVersion=" + preVersion + ", currVersion=" + currVersion
            + ", postVersion=" + postVersion + ", versionDate=" + versionDate + "]";
    }
}
