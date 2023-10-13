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
import io.github.fsixteen.data.jpa.base.generator.groups.DeleteGroup;
import io.github.fsixteen.data.jpa.base.generator.service.BaseDeleteService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 基础删除Controller.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public interface BaseDeleteController<SI extends BaseDeleteService<T, ID, D>, T extends IdEntity<ID>, ID extends Serializable, D extends IdEntity<ID>>
    extends BaseCommonController {

    public SI getService();

    @ApiOperation(value = "删除内容", notes = "删除内容")
    @Operation(summary = "删除内容", description = "删除内容")
    @PostMapping(value = "delete")
    default SimpleResponse<T> delete(HttpServletRequest request, HttpServletResponse response,
        @Validated(value = { DeleteGroup.class }) @RequestBody final D data) {
        return Ok.delete(this.getService().delete(data));
    }

}
