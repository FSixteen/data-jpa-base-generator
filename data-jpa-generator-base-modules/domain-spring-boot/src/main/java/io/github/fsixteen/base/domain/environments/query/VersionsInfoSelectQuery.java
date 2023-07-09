package io.github.fsixteen.base.domain.environments.query;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Between;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal;
import io.github.fsixteen.data.jpa.generator.base.query.DefaultPageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 请求实体: 查询请求: 系统配置信息-模块版本信息.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@ApiModel(value = "请求查询实体-系统配置信息-模块版本信息")
@Schema(description = "请求查询实体-系统配置信息-模块版本信息")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "hibernateLazyInitializer", "handler" })
public class VersionsInfoSelectQuery extends DefaultPageRequest {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = false, hidden = false, example = "110", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Equal
    private Integer id;

    @Schema(description = "模块编码", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "FSN_VERSIONS",
        accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "模块编码", required = false, hidden = false, example = "FSN_VERSIONS",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Equal
    private String code;

    @Schema(description = "模块版本", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "1.0.1", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "模块版本", required = false, hidden = false, example = "1.0.1", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Equal
    private String currVersion;

    @Schema(description = "模块发布日期", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "1685090852009",
        accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "模块发布日期", required = false, hidden = false, example = "1685090852009",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Between(field = "versionDate")
    private Long[] versionDates;

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

    public String getCurrVersion() {
        return currVersion;
    }

    public void setCurrVersion(String currVersion) {
        this.currVersion = currVersion;
    }

    public Long[] getVersionDates() {
        return versionDates;
    }

    public void setVersionDates(Long[] versionDates) {
        this.versionDates = versionDates;
    }

    @Override
    public String toString() {
        return VersionsInfoSelectQuery.class.getSimpleName() + " [id=" + id + ", code=" + code + ", currVersion=" + currVersion + ", versionDates="
            + Arrays.toString(versionDates) + ", getPage()=" + getPage() + ", getSize()=" + getSize() + "]";
    }
}
