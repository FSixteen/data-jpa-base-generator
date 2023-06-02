package io.github.fsixteen.base.domain.configurations.entities;

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
 * 实体: 系统配置信息-系统参数编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Entity
@Table(name = ConfigurationsKey.TABLE_NAME, indexes = { @Index(name = "idx_1887489b010", unique = false, columnList = "code") })
@org.hibernate.annotations.Table(appliesTo = ConfigurationsKey.TABLE_NAME, comment = ConfigurationsKey.TABLE_DESC)
@ApiModel(value = ConfigurationsKey.TABLE_DESC)
@Schema(description = ConfigurationsKey.TABLE_DESC)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
@Where(clause = "deleted = false")
public class ConfigurationsKey extends BaseEntity<Integer> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "fsn_configurations_key";
    public static final String TABLE_DESC = "系统配置信息-系统参数编码";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, length = 0, precision = 11, scale = 0,
        columnDefinition = "int(11) NOT NULL AUTO_INCREMENT COMMENT '主键'")
    @Schema(description = "主键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = false, hidden = false, example = "110", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Integer id;

    @NotNull
    @Column(name = "[scope]", nullable = false, unique = false, length = 36, precision = 0, scale = 0,
        columnDefinition = "varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'DEFAULT' COMMENT '作用域'")
    @Schema(description = "作用域", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "SYSTEM", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "作用域", required = true, hidden = false, example = "SYSTEM", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String scope;

    @NotNull
    @Column(name = "[code]", nullable = false, unique = false, length = 128, precision = 0, scale = 0,
        columnDefinition = "varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '键编码'")
    @Schema(description = "键编码", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "time.out.ms", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "键编码", required = true, hidden = false, example = "time.out.ms",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String code;

    @NotNull
    @Column(name = "[description]", nullable = false, unique = false, length = 128, precision = 0, scale = 0,
        columnDefinition = "varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '键描述'")
    @Schema(description = "键描述", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "Time Out(ms)", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "键描述", required = true, hidden = false, example = "Time Out",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String description;

    @Column(name = "[default_value]", nullable = true, unique = false, length = 512, precision = 0, scale = 0,
        columnDefinition = "varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '键默认值'")
    @Schema(description = "键默认值", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "30000", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "键默认值", required = false, hidden = false, example = "30000", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String defaultValue;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "ConfigurationsKey [id=" + id + ", scope=" + scope + ", code=" + code + ", description=" + description + ", defaultValue=" + defaultValue
            + ", deleted=" + deleted + ", createTime=" + createTime + ", updateTime=" + updateTime + ", deleteTime=" + deleteTime + "]";
    }
}