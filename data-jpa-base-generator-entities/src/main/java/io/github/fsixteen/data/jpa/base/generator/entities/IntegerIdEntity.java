package io.github.fsixteen.data.jpa.base.generator.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 主键字段-Integer类型.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class IntegerIdEntity implements IdEntity<Integer> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键", requiredMode = RequiredMode.NOT_REQUIRED, example = "110")
    public Integer id;

    public IntegerIdEntity() {
    }

    public IntegerIdEntity(Integer id) {
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

}
