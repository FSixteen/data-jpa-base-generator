package io.github.fsixteen.base.domain.configurations.query;

import javax.validation.constraints.NotNull;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Unique;
import io.github.fsixteen.data.jpa.generator.base.entities.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 请求实体: 添加请求: 系统配置信息-系统参数编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@ApiModel(value = "请求添加实体-系统配置信息-系统参数编码")
@Schema(description = "请求添加实体-系统配置信息-系统参数编码")
public class ConfigurationsKeyInsertQuery implements Entity {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "* 请指定环境编码")
    @Schema(description = "作用域", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "SYSTEM", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "作用域", required = true, hidden = false, example = "SYSTEM", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    private String scope;

    @NotNull(message = "* 请指定键编码")
    @Schema(description = "键编码", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "time.out.ms", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "键编码", required = true, hidden = false, example = "time.out.ms",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Unique
    private String code;

    @NotNull(message = "* 请指定键描述")
    @Schema(description = "键描述", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "Time Out(ms)", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "键描述", required = true, hidden = false, example = "Time Out",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    private String description;

    @Schema(description = "键默认值", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "30000", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "键默认值", required = false, hidden = false, example = "30000", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    private String defaultValue;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "ConfigurationsKeyInsertQuery [scope=" + scope + ", code=" + code + ", description=" + description + ", defaultValue=" + defaultValue + "]";
    }
}
