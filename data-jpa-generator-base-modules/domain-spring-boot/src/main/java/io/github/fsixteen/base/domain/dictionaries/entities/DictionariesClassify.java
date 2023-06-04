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
 * 实体: 系统字典信息-系统字典分类编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Entity
@Table(name = DictionariesClassify.TABLE_NAME, indexes = { @Index(name = "idx_1886f7aed00", unique = false, columnList = "classify") })
@org.hibernate.annotations.Table(appliesTo = DictionariesClassify.TABLE_NAME, comment = DictionariesClassify.TABLE_DESC)
@ApiModel(value = DictionariesClassify.TABLE_DESC)
@Schema(description = DictionariesClassify.TABLE_DESC)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
@Where(clause = "deleted = false")
public class DictionariesClassify extends BaseEntity<Integer> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "fsn_dictionaries_classify";
    public static final String TABLE_DESC = "系统字典信息-系统字典分类编码";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, length = 0, precision = 11, scale = 0,
        columnDefinition = "int(11) NOT NULL AUTO_INCREMENT COMMENT '主键'")
    @Schema(description = "主键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = false, hidden = false, example = "110", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Integer id;

    @NotNull
    @Column(name = "scope", nullable = false, unique = false, length = 36, precision = 0, scale = 0,
        columnDefinition = "varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'default' COMMENT '作用域'")
    @Schema(description = "作用域", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "DEFAULT", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "作用域", required = true, hidden = false, example = "DEFAULT", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String scope;

    @NotNull
    @Column(name = "classify", nullable = false, unique = false, length = 36, precision = 0, scale = 0,
        columnDefinition = "varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '字典分类'")
    @Schema(description = "字典分类", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "GENDER", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典分类", required = true, hidden = false, example = "GENDER", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String classify;

    @NotNull
    @Column(name = "description", nullable = false, unique = false, length = 128, precision = 0, scale = 0,
        columnDefinition = "varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT NULL COMMENT '字典描述'")
    @Schema(description = "字典描述", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "性别(GB2261-1980)",
        accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典描述", required = true, hidden = false, example = "性别(GB2261-1980)",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String description;

    @Column(name = "default_key", nullable = true, unique = false, length = 32, precision = 0, scale = 0,
        columnDefinition = "varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字典默认键'")
    @Schema(description = "字典默认键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "0", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典默认键", required = false, hidden = false, example = "0", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String defaultKey;

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

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultKey() {
        return defaultKey;
    }

    public void setDefaultKey(String defaultKey) {
        this.defaultKey = defaultKey;
    }

    @Override
    public String toString() {
        return DictionariesClassify.class.getSimpleName() + " [id=" + id + ", scope=" + scope + ", classify=" + classify + ", description=" + description
            + ", defaultKey=" + defaultKey + ", deleted=" + deleted + ", createTime=" + createTime + ", updateTime=" + updateTime + ", deleteTime=" + deleteTime
            + "]";
    }
}