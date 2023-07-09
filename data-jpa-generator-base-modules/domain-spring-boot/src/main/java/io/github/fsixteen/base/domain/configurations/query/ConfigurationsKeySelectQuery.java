package io.github.fsixteen.base.domain.configurations.query;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SplitIn;
import io.github.fsixteen.data.jpa.generator.base.query.DefaultPageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 请求实体: 查询请求: 系统配置信息-系统参数编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@ApiModel(value = "请求查询实体-系统配置信息-系统参数编码")
@Schema(description = "请求查询实体-系统配置信息-系统参数编码")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "hibernateLazyInitializer", "handler" })
public class ConfigurationsKeySelectQuery extends DefaultPageRequest {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = false, hidden = false, example = "110", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Equal
    private Integer id;

    @Schema(description = "作用域", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "SYSTEM", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "作用域", required = false, hidden = false, example = "SYSTEM", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @SplitIn
    private String scope;

    @Schema(description = "键编码", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "time.out.ms",
        accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "键编码", required = false, hidden = false, example = "time.out.ms",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Equal
    private String code;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return ConfigurationsKeySelectQuery.class.getSimpleName() + " [id=" + id + ", scope=" + scope + ", code=" + code + "]";
    }
}
