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
 * 实体: 系统配置信息-模块版本信息.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Entity
@Table(name = VersionsInfo.TABLE_NAME, indexes = { @Index(name = "idx_188572aa166", unique = true, columnList = "code,curr_version") })
@org.hibernate.annotations.Table(appliesTo = VersionsInfo.TABLE_NAME, comment = VersionsInfo.TABLE_DESC)
@ApiModel(value = VersionsInfo.TABLE_DESC)
@Schema(description = VersionsInfo.TABLE_DESC)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
@Where(clause = "deleted = false")
public class VersionsInfo extends BaseEntity<Integer> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "fsn_versions_info";
    public static final String TABLE_DESC = "系统配置信息-模块版本信息";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, length = 0, precision = 11, scale = 0,
        columnDefinition = "int(11) NOT NULL AUTO_INCREMENT COMMENT '主键'")
    @Schema(description = "主键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = false, hidden = false, example = "110", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Integer id;

    @NotNull
    @Column(name = "code", nullable = false, unique = false, length = 64, precision = 0, scale = 0,
        columnDefinition = "varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '模块编码'")
    @Schema(description = "模块编码", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "FSN_VERSIONS", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "模块编码", required = true, hidden = false, example = "FSN_VERSIONS",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String code;

    @NotNull
    @Column(name = "description", nullable = false, unique = false, length = 128, precision = 0, scale = 0,
        columnDefinition = "varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '模块描述'")
    @Schema(description = "模块描述", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "模块版本信息", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "模块描述", required = true, hidden = false, example = "模块版本信息", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String description;

    @Column(name = "pre_version", nullable = true, unique = false, length = 64, precision = 0, scale = 0,
        columnDefinition = "varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '模块上一版本'")
    @Schema(description = "模块上一版本", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "1.0.0", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "模块上一版本", required = true, hidden = false, example = "1.0.0",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String preVersion;

    @NotNull
    @Column(name = "curr_version", nullable = false, unique = false, length = 64, precision = 0, scale = 0,
        columnDefinition = "varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'unknown' COMMENT '模块版本'")
    @Schema(description = "模块版本", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "1.0.1", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "模块版本", required = true, hidden = false, example = "1.0.1", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String currVersion;

    @Column(name = "post_version", nullable = true, unique = false, length = 64, precision = 0, scale = 0,
        columnDefinition = "varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '模块下一版本'")
    @Schema(description = "模块下一版本", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "1.0.2", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "模块下一版本", required = false, hidden = false, example = "1.0.2",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String postVersion;

    @NotNull
    @Column(name = "version_date", nullable = false, unique = false, length = 0, precision = 20, scale = 0,
        columnDefinition = "bigint(20) NOT NULL DEFAULT '0' COMMENT '模块发布日期'")
    @Schema(description = "模块发布日期", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "1685090852009",
        accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "模块发布日期", required = true, hidden = false, example = "1685090852009",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Long versionDate;

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

    public String getPreVersion() {
        return preVersion;
    }

    public void setPreVersion(String preVersion) {
        this.preVersion = preVersion;
    }

    public String getCurrVersion() {
        return currVersion;
    }

    public void setCurrVersion(String currVersion) {
        this.currVersion = currVersion;
    }

    public String getPostVersion() {
        return postVersion;
    }

    public void setPostVersion(String postVersion) {
        this.postVersion = postVersion;
    }

    public Long getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(Long versionDate) {
        this.versionDate = versionDate;
    }

    @Override
    public String toString() {
        return "FsnVersionsInfo [id=" + id + ", code=" + code + ", description=" + description + ", preVersion=" + preVersion + ", currVersion=" + currVersion
            + ", postVersion=" + postVersion + ", versionDate=" + versionDate + ", deleted=" + deleted + ", createTime=" + createTime + ", updateTime="
            + updateTime + ", deleteTime=" + deleteTime + "]";
    }
}