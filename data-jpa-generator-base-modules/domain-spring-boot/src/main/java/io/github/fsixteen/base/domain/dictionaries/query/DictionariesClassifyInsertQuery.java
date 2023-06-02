package io.github.fsixteen.base.domain.dictionaries.query;

import javax.validation.constraints.NotNull;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Unique;
import io.github.fsixteen.data.jpa.generator.base.entities.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 请求实体: 添加请求: 系统字典信息-系统字典分类编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@ApiModel(value = "请求添加实体-系统字典信息-系统字典分类编码")
@Schema(description = "请求添加实体-系统字典信息-系统字典分类编码")
public class DictionariesClassifyInsertQuery implements Entity {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "* 请指定作用域")
    @Schema(description = "作用域", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "DEFAULT", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "作用域", required = true, hidden = false, example = "DEFAULT")
    private String scope;

    @NotNull(message = "* 请指字典分类")
    @Schema(description = "字典分类", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "GENDER", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典分类", required = true, hidden = false, example = "GENDER")
    @Unique
    private String classify;

    @NotNull(message = "* 请指字典描述")
    @Schema(description = "字典描述", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "性别(GB2261-1980)",
        accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典描述", required = true, hidden = false, example = "性别(GB2261-1980)")
    private String description;

    @Schema(description = "字典默认键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "0", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典默认键", required = false, hidden = false, example = "0")
    private String defaultKey;

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
        return "DictionariesClassifyInsertQuery [scope=" + scope + ", classify=" + classify + ", description=" + description + ", defaultKey=" + defaultKey
            + "]";
    }
}
