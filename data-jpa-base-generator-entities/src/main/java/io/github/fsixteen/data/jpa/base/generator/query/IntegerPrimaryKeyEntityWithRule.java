package io.github.fsixteen.data.jpa.base.generator.query;

import io.github.fsixteen.data.jpa.base.generator.rules.NumberAutoIncrementIdRule;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 请求实体: 单主键请求.<br>
 *
 * @author FSixteen
 * @since 1.0.1
 */
@Schema(description = "请求实体-单主键请求")
public class IntegerPrimaryKeyEntityWithRule implements NumberAutoIncrementIdRule<Integer> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键", requiredMode = RequiredMode.REQUIRED, example = "110")
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