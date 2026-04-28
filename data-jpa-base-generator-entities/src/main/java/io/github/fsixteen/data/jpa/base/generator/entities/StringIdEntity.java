package io.github.fsixteen.data.jpa.base.generator.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 主键字段-String类型.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class StringIdEntity implements IdEntity<String> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键", requiredMode = RequiredMode.NOT_REQUIRED, example = "110")
    public String id;

    public StringIdEntity() {
    }

    public StringIdEntity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
