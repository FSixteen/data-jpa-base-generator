package io.github.fsixteen.base.domain.configurations.query;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Like;
import io.github.fsixteen.data.jpa.generator.base.query.DefaultPageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 请求实体: 查询请求: 系统配置信息-系统参数.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@ApiModel(value = "请求查询实体-系统配置信息-系统参数")
@Schema(description = "请求查询实体-系统配置信息-系统参数")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "hibernateLazyInitializer", "handler" })
public class ConfigurationsInfoSelectQuery extends DefaultPageRequest {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = false, hidden = false, example = "110", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Equal
    private Integer id;

    @Schema(description = "环境编码", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "DEFAULT", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "环境编码", required = false, hidden = false, example = "DEFAULT",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Equal
    private String env;

    @Schema(description = "键编码", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "time.out.ms",
        accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "键编码", required = false, hidden = false, example = "time.out.ms",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Like
    private String key;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return ConfigurationsInfoSelectQuery.class.getSimpleName() + " [id=" + id + ", env=" + env + ", key=" + key + "]";
    }
}
