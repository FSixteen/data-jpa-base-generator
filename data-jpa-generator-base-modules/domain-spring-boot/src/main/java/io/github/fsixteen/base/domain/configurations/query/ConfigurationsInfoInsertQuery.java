package io.github.fsixteen.base.domain.configurations.query;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Unique;
import io.github.fsixteen.data.jpa.generator.base.entities.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 请求实体: 添加请求: 系统配置信息-系统参数.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@ApiModel(value = "请求添加实体-系统配置信息-系统参数")
@Schema(description = "请求添加实体-系统配置信息-系统参数")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "hibernateLazyInitializer", "handler" })
public class ConfigurationsInfoInsertQuery implements Entity {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "* 请指定环境编码")
    @Schema(description = "环境编码", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "DEFAULT", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "环境编码", required = true, hidden = false, example = "DEFAULT",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Unique
    private String env;

    @NotNull(message = "* 请指定键编码")
    @Schema(description = "键编码", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "time.out.ms", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "键编码", required = true, hidden = false, example = "time.out.ms",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Unique
    private String key;

    @NotNull(message = "* 请指定键值")
    @Schema(description = "键值", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "30000", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "键值", required = true, hidden = false, example = "30000", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    private String val;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return ConfigurationsInfoInsertQuery.class.getSimpleName() + " [env=" + env + ", key=" + key + ", val=" + val + "]";
    }
}
