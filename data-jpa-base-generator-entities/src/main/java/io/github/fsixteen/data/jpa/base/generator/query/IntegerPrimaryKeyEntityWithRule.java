package io.github.fsixteen.data.jpa.base.generator.query;

import io.github.fsixteen.data.jpa.base.generator.rules.NumberAutoIncrementIdRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 请求实体: 单主键请求.<br>
 *
 * @author FSixteen
 * @since 1.0.1
 */
@ApiModel(value = "请求实体-单主键请求")
@Schema(description = "请求实体-单主键请求")
public class IntegerPrimaryKeyEntityWithRule implements NumberAutoIncrementIdRule<Integer> {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = true, hidden = false, example = "110", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    private Integer id;

    public IntegerPrimaryKeyEntityWithRule() {
    }

    public IntegerPrimaryKeyEntityWithRule(Integer id) {
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

    @Override
    public String toString() {
        return IntegerPrimaryKeyEntityWithRule.class.getSimpleName() + " [id=" + id + "]";
    }

}