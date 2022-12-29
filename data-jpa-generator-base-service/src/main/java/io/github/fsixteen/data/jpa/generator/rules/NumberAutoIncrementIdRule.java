package io.github.fsixteen.data.jpa.generator.rules;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import io.github.fsixteen.data.jpa.generator.base.entities.IdEntity;
import io.github.fsixteen.data.jpa.generator.base.groups.IdGroup;
import io.github.fsixteen.data.jpa.generator.base.groups.InsertGroup;

/**
 * 主键字段自增规则.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
public interface NumberAutoIncrementIdRule<ID extends Number> extends IdEntity<ID> {

    @Null(groups = { InsertGroup.class }, message = "该操作不需要指定主键信息")
    @NotNull(groups = { IdGroup.class }, message = "该操作需要指定主键信息")
    @Min(value = 1, message = "无效主键信息")
    public ID getId();

}
