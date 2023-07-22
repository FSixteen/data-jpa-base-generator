package io.github.fsixteen.base.domain.dictionaries.query;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SplitIn;
import io.github.fsixteen.data.jpa.generator.base.query.DefaultPageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 请求实体: 查询请求: 系统字典信息-系统字典分类编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@ApiModel(value = "请求查询实体-系统字典信息-系统字典分类编码")
@Schema(description = "请求查询实体-系统字典信息-系统字典分类编码")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "hibernateLazyInitializer", "handler" })
public class DictionariesClassifySelectQuery extends DefaultPageRequest {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "110", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "主键", required = false, hidden = false, example = "110")
    @Equal
    private Integer id;

    @Schema(description = "作用域", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "DEFAULT", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "作用域", required = false, hidden = false, example = "DEFAULT")
    @SplitIn
    private String scope;

    @Schema(description = "字典分类", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "GENDER", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "字典分类", required = false, hidden = false, example = "GENDER")
    @SplitIn
    private String classify;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DictionariesClassifySelectQuery withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public DictionariesClassifySelectQuery withScope(String scope) {
        this.scope = scope;
        return this;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public DictionariesClassifySelectQuery withClassify(String classify) {
        this.classify = classify;
        return this;
    }

    @Override
    public String toString() {
        return DictionariesClassifySelectQuery.class.getSimpleName() + " [id=" + id + ", scope=" + scope + ", classify=" + classify + "]";
    }
}
