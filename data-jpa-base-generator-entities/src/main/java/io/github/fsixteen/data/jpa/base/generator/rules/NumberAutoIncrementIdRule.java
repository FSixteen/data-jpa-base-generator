package io.github.fsixteen.data.jpa.base.generator.rules;

import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;
import io.github.fsixteen.data.jpa.base.generator.groups.IdGroup;
import io.github.fsixteen.data.jpa.base.generator.groups.InsertGroup;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

/**
 * 主键字段自增规则.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public interface NumberAutoIncrementIdRule<ID extends Number> extends IdEntity<ID> {

    @Null(groups = { InsertGroup.class }, message = "该操作不需要指定主键信息")
    @NotNull(groups = { IdGroup.class }, message = "该操作需要指定主键信息")
    @Min(value = 1, message = "无效主键信息")
    public ID getId();

}
