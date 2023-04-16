package io.github.fsixteen.data.jpa.generator.base.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.github.fsixteen.common.structure.Response;
import io.github.fsixteen.common.structure.Response.SimpleResponse;
import io.github.fsixteen.common.structure.extend.Ok;
import io.github.fsixteen.data.jpa.generator.base.entities.Entity;
import io.github.fsixteen.data.jpa.generator.base.entities.IdEntity;
import io.github.fsixteen.data.jpa.generator.base.service.BaseSelectService;
import io.github.fsixteen.data.jpa.generator.beans.BasePageRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 基础查询Controller.<br>
 * 
 * @author FSixteen
 * @since V1.0.0
 */
public interface BaseSelectController<SI extends BaseSelectService<T, ID, S>, T extends IdEntity<ID>, ID extends Serializable, S extends Entity & BasePageRequest>
        extends BaseCommonController {

    static final Logger log = LoggerFactory.getLogger(BaseSelectController.class);

    public SI getService();

    /**
     * 后置处理器.
     *
     * @return BiConsumer&lt;List&lt;T&gt;, Response&lt;List&lt;T&gt;, Void&gt;&gt;
     */
    default BiConsumer<List<T>, Response<List<T>, Void>> selectPostprocessor() {
        return (e, r) -> {
            if (log.isDebugEnabled()) {
                log.debug("Select Post Processor Nothing.");
            }
        };
    }

    @ApiOperation(value = "分页查询", notes = "分页查询")
    @Operation(summary = "分页查询", description = "分页查询")
    @PostMapping(value = "select")
    default Response<List<T>, Void> select(HttpServletRequest request, HttpServletResponse response, @RequestBody final S data) {
        Page<T> eles = this.getService().select(data);
        SimpleResponse<List<T>> result = Ok.select(eles.getContent(), data.getPage(), data.getSize(), eles.getTotalElements());
        Optional.ofNullable(this.selectPostprocessor()).ifPresent(it -> it.accept(eles.getContent(), result));
        return result;
    }

    @ApiOperation(value = "完整查询", notes = "完整查询", hidden = true)
    @Operation(summary = "完整查询", description = "完整查询", hidden = true)
    @PostMapping(value = "select/all")
    default Response<List<T>, Void> selectAll(HttpServletRequest request, HttpServletResponse response) {
        List<T> eles = this.getService().selectAll();
        SimpleResponse<List<T>> result = Ok.select(eles, eles.size());
        Optional.ofNullable(this.selectPostprocessor()).ifPresent(it -> it.accept(eles, result));
        return result;
    }

}
