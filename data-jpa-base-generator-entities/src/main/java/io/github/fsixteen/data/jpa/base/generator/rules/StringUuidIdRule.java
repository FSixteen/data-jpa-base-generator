package io.github.fsixteen.data.jpa.base.generator.rules;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;
import io.github.fsixteen.data.jpa.base.generator.groups.IdGroup;
import io.github.fsixteen.data.jpa.base.generator.groups.InsertGroup;

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
