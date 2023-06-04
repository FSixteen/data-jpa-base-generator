package io.github.fsixteen.base.domain.environments.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.github.fsixteen.data.jpa.generator.base.entities.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 实体: 系统配置信息-系统环境编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Entity
@Table(name = EnvironmentsInfo.TABLE_NAME, indexes = { @Index(name = "idx_18837e9ca94", unique = true, columnList = "code") })
@org.hibernate.annotations.Table(appliesTo = EnvironmentsInfo.TABLE_NAME, comment = EnvironmentsInfo.TABLE_DESC)
@ApiModel(value = EnvironmentsInfo.TABLE_DESC)
@Schema(description = EnvironmentsInfo.TABLE_DESC)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
@Where(clause = "deleted = false")
public class EnvironmentsInfo extends BaseEntity<Integer> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "fsn_environments_info";
    public static final String TABLE_DESC = "系统配置信息-系统环境编码";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, length = 0, precision = 11, scale = 0,
        columnDefinition = "int(11) NOT NULL AUTO_INCREMENT COMMENT '主键'")
    @Schema(description = "主键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = false, hidden = false, example = "110", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Integer id;

    @NotNull
    @Column(name = "code", nullable = false, unique = false, length = 36, precision = 0, scale = 0,
        columnDefinition = "varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '环境编码'")
    @Schema(description = "环境编码", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "DEVEL", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "环境编码", required = true, hidden = false, example = "DEVEL", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String code;

    @NotNull
    @Column(name = "description", nullable = false, unique = false, length = 128, precision = 0, scale = 0,
        columnDefinition = "varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '环境描述'")
    @Schema(description = "环境描述", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "开发环境", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "环境描述", required = true, hidden = false, example = "开发环境", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String description;

    @NotNull
    @Column(name = "enabled", nullable = false, unique = false, length = 0, precision = 1, scale = 0,
        columnDefinition = "tinyint(1) NOT NULL DEFAULT '0' COMMENT '启用状态'")
    @Schema(description = "启用状态", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "false", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "启用状态", required = true, hidden = false, example = "false", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Boolean enabled;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return EnvironmentsInfo.class.getSimpleName() + " [id=" + id + ", code=" + code + ", description=" + description + ", enabled=" + enabled + ", deleted="
            + deleted + ", createTime=" + createTime + ", updateTime=" + updateTime + ", deleteTime=" + deleteTime + "]";
    }
}