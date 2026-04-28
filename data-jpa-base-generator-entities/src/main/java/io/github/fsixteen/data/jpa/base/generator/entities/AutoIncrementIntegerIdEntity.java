package io.github.fsixteen.data.jpa.base.generator.entities;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 自增主键-Integer类型.<br>
 *
 * @author FSixteen
 * @since 1.0.1
 */
public class AutoIncrementIntegerIdEntity implements IdEntity<Integer> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, length = 0, precision = 11, scale = 0,
        columnDefinition = "int(11) NOT NULL AUTO_INCREMENT COMMENT '主键'")
    @Schema(description = "主键", requiredMode = RequiredMode.NOT_REQUIRED, example = "110")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    public Integer id;

    public AutoIncrementIntegerIdEntity() {
    }

    public AutoIncrementIntegerIdEntity(Integer id) {
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
