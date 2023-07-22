package io.github.fsixteen.base.domain.configurations.query;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.github.fsixteen.data.jpa.generator.base.entities.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 请求实体: 更新请求: 系统配置信息-系统参数编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@ApiModel(value = "请求更新实体-系统配置信息-系统参数编码")
@Schema(description = "请求更新实体-系统配置信息-系统参数编码")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "hibernateLazyInitializer", "handler" })
public class ConfigurationsKeyUpdateQuery extends ConfigurationsKeyInsertQuery implements IdEntity<Integer> {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "* 请指定该主键")
    @Schema(description = "主键", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = true, hidden = false, example = "110", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    private Integer id;

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public ConfigurationsKeyUpdateQuery withId(Integer id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return ConfigurationsKeyUpdateQuery.class.getSimpleName() + " [id=" + id + ", getScope()=" + getScope() + ", getCode()=" + getCode()
            + ", getDescription()=" + getDescription() + ", getDefaultValue()=" + getDefaultValue() + ", toString()=" + super.toString() + ", getClass()="
            + getClass() + ", hashCode()=" + hashCode() + "]";
    }
}
