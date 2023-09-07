package io.github.fsixteen.data.jpa.generator.base.controller;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.github.fsixteen.common.structure.Response.SimpleResponse;
import io.github.fsixteen.common.structure.extend.Ok;
import io.github.fsixteen.data.jpa.generator.base.entities.Entity;
import io.github.fsixteen.data.jpa.generator.base.entities.IdEntity;
import io.github.fsixteen.data.jpa.generator.base.groups.InsertGroup;
import io.github.fsixteen.data.jpa.generator.base.service.BaseInsertService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 基础添加Controller.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public interface BaseInsertController<SI extends BaseInsertService<T, ID, I>, T extends IdEntity<ID>, ID extends Serializable, I extends Entity>
    extends BaseCommonController {

    public SI getService();

    @ApiOperation(value = "添加内容", notes = "添加内容")
    @Operation(summary = "添加内容", description = "添加内容")
    @PostMapping(value = "insert")
    default SimpleResponse<T> insert(HttpServletRequest request, HttpServletResponse response,
        @Validated(value = { InsertGroup.class }) @RequestBody final I data) {
        return Ok.insert(this.getService().insert(data));
    }

}
