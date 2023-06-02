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
 * 请求实体: 添加请求: 系统字典信息-系统字典.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@ApiModel(value = "请求添加实体-系统字典信息-系统字典")
@Schema(description = "请求添加实体-系统字典信息-系统字典")
public class DictionariesInfoInsertQuery implements Entity {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "* 请指定字典环境编码")
    @Schema(description = "字典环境编码", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "DEFAULT", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典环境编码", required = true, hidden = false, example = "DEFAULT")
    @Unique
    private String env;

    @NotNull(message = "* 请指定字典分类编码")
    @Schema(description = "字典分类编码", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "GENDER", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典分类编码", required = true, hidden = false, example = "GENDER")
    @Unique
    private String classify;

    @NotNull(message = "* 请指定字典健")
    @Schema(description = "字典健", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "0", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典健", required = true, hidden = false, example = "0")
    @Unique
    private String dictKey;

    @NotNull(message = "* 请指定字典值")
    @Schema(description = "字典值", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "未知的性别", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典值", required = true, hidden = false, example = "未知的性别")
    @Unique
    private String dictValue;

    @Schema(description = "启用状态", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "false", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "启用状态", required = false, hidden = false, example = "false", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    private boolean enabled = true;

    @Schema(description = "排序", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "1", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "排序", required = false, hidden = false, example = "1", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    private int ordered = 0;

    @NotNull(message = "* 请指字典描述")
    @Schema(description = "字典描述", required = true, requiredMode = RequiredMode.REQUIRED, hidden = false, example = "性别(GB2261-1980)",
        accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典描述", required = true, hidden = false, example = "性别(GB2261-1980)")
    private String description;

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getOrdered() {
        return ordered;
    }

    public void setOrdered(int ordered) {
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
        return "DictionariesInfoInsertQuery [env=" + env + ", classify=" + classify + ", dictKey=" + dictKey + ", dictValue=" + dictValue + ", enabled="
            + enabled + ", ordered=" + ordered + ", description=" + description + "]";
    }
}
