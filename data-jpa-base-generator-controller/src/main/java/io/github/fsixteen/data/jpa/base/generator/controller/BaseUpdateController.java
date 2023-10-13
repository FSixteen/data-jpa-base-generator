package io.github.fsixteen.data.jpa.base.generator.controller;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.github.fsixteen.common.structure.Response.SimpleResponse;
import io.github.fsixteen.common.structure.extend.Ok;
import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;
import io.github.fsixteen.data.jpa.base.generator.groups.UpdateGroup;
import io.github.fsixteen.data.jpa.base.generator.service.BaseUpdateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 基础更新Controller.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public interface BaseUpdateController<SI extends BaseUpdateService<T, ID, U>, T extends IdEntity<ID>, ID extends Serializable, U extends IdEntity<ID>>
    extends BaseCommonController {

    public SI getService();

    @ApiOperation(value = "修改内容", notes = "修改内容")
    @Operation(summary = "修改内容", description = "修改内容")
    @PostMapping(value = "update")
    default SimpleResponse<T> update(HttpServletRequest request, HttpServletResponse response,
        @Validated(value = { UpdateGroup.class }) @RequestBody final U data) {
        return Ok.update(this.getService().update(data));
    }

}
