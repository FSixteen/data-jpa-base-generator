package io.github.fsixteen.data.jpa.generator.base.entities;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 通用字段.<br>
 *
 * @author FSixteen
 * @since 1.0.1
 */
@MappedSuperclass
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "deleted", "createTime", "updateTime", "deleteTime" })
@Where(clause = "deleted = false")
public abstract class BaseEntityWithAutoIncrementIntegerId extends BaseEntity<Integer> {
    private static final long serialVersionUID = 1L;

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, length = 0, precision = 11, scale = 0,
        columnDefinition = "int(11) NOT NULL AUTO_INCREMENT COMMENT '主键'")
    @Schema(description = "主键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = false, hidden = false, example = "110", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    public Integer id;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

}