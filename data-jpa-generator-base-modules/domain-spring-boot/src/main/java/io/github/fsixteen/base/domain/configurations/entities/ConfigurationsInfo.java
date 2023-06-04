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
 * 实体: 系统配置信息-系统参数.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Entity
@Table(name = ConfigurationsInfo.TABLE_NAME,
    indexes = { @Index(name = "idx_18069ff9a81", unique = false, columnList = "env"), @Index(name = "idx_1886f900269", unique = false, columnList = "key"),
        @Index(name = "idx_188748c26d6", unique = true, columnList = "env,key,deleted,delete_time") })
@org.hibernate.annotations.Table(appliesTo = ConfigurationsInfo.TABLE_NAME, comment = ConfigurationsInfo.TABLE_DESC)
@ApiModel(value = ConfigurationsInfo.TABLE_DESC)
@Schema(description = ConfigurationsInfo.TABLE_DESC)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
@Where(clause = "deleted = false")
public class ConfigurationsInfo extends BaseEntity<Integer> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "fsn_configurations_info";
    public static final String TABLE_DESC = "系统配置信息-系统参数";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "[id]", nullable = false, unique = true, length = 0, precision = 11, scale = 0,
        columnDefinition = "int(11) NOT NULL AUTO_INCREMENT COMMENT '主键'")
    @Schema(description = "主键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = false, hidden = false, example = "110", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Integer id;

    @NotNull
    @Column(name = "[env]", nullable = false, unique = false, length = 36, precision = 0, scale = 0,
        columnDefinition = "varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '环境编码'")
    @Schema(description = "环境编码", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "DEFAULT", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "环境编码", required = true, hidden = false, example = "DEFAULT",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String env;

    @NotNull
    @Column(name = "[key]", nullable = false, unique = false, length = 128, precision = 0, scale = 0,
        columnDefinition = "varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '键编码'")
    @Schema(description = "键编码", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "time.out.ms", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "键编码", required = true, hidden = false, example = "time.out.ms",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String key;

    @NotNull
    @Column(name = "[val]", nullable = false, unique = false, length = 65535, precision = 0, scale = 0,
        columnDefinition = "text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '键值'")
    @Schema(description = "键值", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "30000", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "键值", required = true, hidden = false, example = "30000", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String val;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return ConfigurationsInfo.class.getSimpleName() + " [id=" + id + ", env=" + env + ", key=" + key + ", val=" + val + ", deleted=" + deleted
            + ", createTime=" + createTime + ", updateTime=" + updateTime + ", deleteTime=" + deleteTime + "]";
    }
}