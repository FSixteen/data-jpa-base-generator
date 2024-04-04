package io.github.fsixteen.data.jpa.base.generator.rules;

import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;
import io.github.fsixteen.data.jpa.base.generator.groups.IdGroup;
import io.github.fsixteen.data.jpa.base.generator.groups.InsertGroup;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

/**
 * 主键UUID规则.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public interface StringUuidIdRule extends IdEntity<String> {

    @Null(groups = { InsertGroup.class }, message = "该操作不需要指定主键信息")
    @NotNull(groups = { IdGroup.class }, message = "该操作需要指定主键信息")
    @Size(min = 1, max = 36, message = "无效主键信息")
    public String getId();

}
