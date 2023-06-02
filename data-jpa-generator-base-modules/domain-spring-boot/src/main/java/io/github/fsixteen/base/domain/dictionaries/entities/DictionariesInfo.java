package io.github.fsixteen.base.domain.dictionaries.entities;

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
 * 实体: 系统字典信息-系统字典.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Entity
@Table(name = DictionariesInfo.TABLE_NAME,
    indexes = { @Index(name = "idx_18069ff9a81", unique = false, columnList = "classify"), @Index(name = "idx_1886f900269", unique = false, columnList = "env"),
        @Index(name = "idx_1886f8fb164", unique = true, columnList = "env,classify,dict_key,dict_value") })
@org.hibernate.annotations.Table(appliesTo = DictionariesInfo.TABLE_NAME, comment = DictionariesInfo.TABLE_DESC)
@ApiModel(value = DictionariesInfo.TABLE_DESC)
@Schema(description = DictionariesInfo.TABLE_DESC)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
@Where(clause = "deleted = false")
public class DictionariesInfo extends BaseEntity<Integer> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "fsn_dictionaries_info";
    public static final String TABLE_DESC = "系统字典信息-系统字典";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, length = 0, precision = 11, scale = 0,
        columnDefinition = "int(11) NOT NULL AUTO_INCREMENT COMMENT '主键'")
    @Schema(description = "主键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = false, hidden = false, example = "110", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Integer id;

    @NotNull
    @Column(name = "env", nullable = false, unique = false, length = 36, precision = 0, scale = 0,
        columnDefinition = "varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '字典环境编码'")
    @Schema(description = "字典环境编码", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "DEFAULT", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典环境编码", required = true, hidden = false, example = "DEFAULT",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String env;

    @NotNull
    @Column(name = "classify", nullable = false, unique = false, length = 36, precision = 0, scale = 0,
        columnDefinition = "varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '字典分类编码'")
    @Schema(description = "字典分类编码", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "GENDER", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典分类编码", required = true, hidden = false, example = "GENDER",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String classify;

    @NotNull
    @Column(name = "dict_key", nullable = false, unique = false, length = 128, precision = 0, scale = 0,
        columnDefinition = "varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '字典健'")
    @Schema(description = "字典健", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "0", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典健", required = true, hidden = false, example = "0", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String dictKey;

    @NotNull
    @Column(name = "dict_value", nullable = false, unique = false, length = 512, precision = 0, scale = 0,
        columnDefinition = "varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '字典值'")
    @Schema(description = "字典值", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "未知的性别", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典值", required = true, hidden = false, example = "未知的性别", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String dictValue;

    @NotNull
    @Column(name = "enabled", nullable = false, unique = false, length = 0, precision = 1, scale = 0,
        columnDefinition = "tinyint(1) NOT NULL DEFAULT '1' COMMENT '启用状态'")
    @Schema(description = "启用状态", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "false", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "启用状态", required = true, hidden = false, example = "false", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Boolean enabled;

    @NotNull
    @Column(name = "ordered", nullable = false, unique = false, length = 0, precision = 11, scale = 0,
        columnDefinition = "int(11) NOT NULL DEFAULT '0' COMMENT '排序'")
    @Schema(description = "排序", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "1", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "排序", required = true, hidden = false, example = "1", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Integer ordered;

    @NotNull
    @Column(name = "description", nullable = false, unique = false, length = 128, precision = 0, scale = 0,
        columnDefinition = "varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '字典描述'")
    @Schema(description = "字典描述", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "性别(GB2261-1980)",
        accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典描述", required = true, hidden = false, example = "性别(GB2261-1980)",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String description;

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

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getDictKey() {
        return dictKey;
    }

    public void setDictKey(String dictKey) {
        this.dictKey = dictKey;
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getOrdered() {
        return ordered;
    }

    public void setOrdered(Integer ordered) {
        this.ordered = ordered;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DictionariesInfo [id=" + id + ", env=" + env + ", classify=" + classify + ", dictKey=" + dictKey + ", dictValue=" + dictValue + ", deleted="
            + deleted + ", createTime=" + createTime + ", updateTime=" + updateTime + ", deleteTime=" + deleteTime + ", enabled=" + enabled + ", ordered="
            + ordered + ", description=" + description + "]";
    }
}