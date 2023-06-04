package io.github.fsixteen.data.jpa.generator.base.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.github.fsixteen.common.structure.Response;
import io.github.fsixteen.common.structure.extend.Err;
import io.github.fsixteen.common.structure.extend.Ok;
import io.github.fsixteen.data.jpa.generator.base.entities.Entity;
import io.github.fsixteen.data.jpa.generator.base.entities.IdEntity;
import io.github.fsixteen.data.jpa.generator.base.groups.SelectGroup;
import io.github.fsixteen.data.jpa.generator.base.query.BasePageRequest;
import io.github.fsixteen.data.jpa.generator.base.service.BaseSelectService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 基础查询Controller.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public interface BaseSelectController<SI extends BaseSelectService<T, ID, S>, T extends IdEntity<ID>, ID extends Serializable,
    S extends Entity & BasePageRequest> extends BaseCommonController {

    static final Logger log = LoggerFactory.getLogger(BaseSelectController.class);

    public SI getService();

    /**
     * 后置处理器.
     *
     * @return BiConsumer&lt;List&lt;T&gt;, Response&lt;List&lt;T&gt;,
     *         Object&gt;&gt;
     */
    default BiConsumer<List<T>, Response<?, Object>> selectPostprocessor() {
        return (e, r) -> {
            if (log.isDebugEnabled()) {
                log.debug("Select Post Processor Nothing.");
            }
        };
    }

    /**
     * 单一详细查询.<br>
     * 
     * @param request  {@link javax.servlet.http.HttpServletRequest}实例, 自动注入
     * @param response {@link javax.servlet.http.HttpServletResponse}实例, 自动注入
     * @param id       请求数据
     * @return Response&lt;List&lt;T&gt;, Object&gt;
     */
    @ApiOperation(value = "单一详细查询", notes = "单一详细查询")
    @Operation(summary = "单一详细查询", description = "单一详细查询")
    @PostMapping(value = "select/{id}")
    default Response<T, Object> select(HttpServletRequest request, HttpServletResponse response,
        @Validated(value = { SelectGroup.class }) @NotNull(message = "请指定查询内容") @PathVariable("id") final ID id) {
        Optional<T> eles = this.getService().findById(id);
        Response<T, Object> result = eles.isPresent() ? Ok.selectWithExts(eles.get(), null) : Err.selectWithExts(null, null);
        if (eles.isPresent()) {
            Optional.ofNullable(this.selectPostprocessor()).ifPresent(it -> it.accept(Arrays.asList(eles.get()), result));
        }
        return result;
    }

    /**
     * 分页查询.<br>
     * 
     * @param request  {@link javax.servlet.http.HttpServletRequest}实例, 自动注入
     * @param response {@link javax.servlet.http.HttpServletResponse}实例, 自动注入
     * @param data     请求实例
     * @return Response&lt;List&lt;T&gt;, Object&gt;
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @Operation(summary = "分页查询", description = "分页查询")
    @PostMapping(value = "select")
    default Response<List<T>, Object> select(HttpServletRequest request, HttpServletResponse response,
        @Validated(value = { SelectGroup.class }) @RequestBody final S data) {
        Page<T> eles = this.getService().select(data);
        Response<List<T>, Object> result = Ok.selectWithExts(eles.getContent(), null, data.getPage(), data.getSize(), eles.getTotalElements());
        Optional.ofNullable(this.selectPostprocessor()).ifPresent(it -> it.accept(eles.getContent(), result));
        return result;
    }

    /**
     * 完整查询.<br>
     * 
     * @param request  {@link javax.servlet.http.HttpServletRequest}实例, 自动注入
     * @param response {@link javax.servlet.http.HttpServletResponse}实例, 自动注入
     * @return Response&lt;List&lt;T&gt;, Object&gt;
     */
    @ApiOperation(value = "完整查询", notes = "完整查询", hidden = true)
    @Operation(summary = "完整查询", description = "完整查询", hidden = true)
    @PostMapping(value = "select/all")
    default Response<List<T>, Object> selectAll(HttpServletRequest request, HttpServletResponse response) {
        List<T> eles = this.getService().selectAll();
        Response<List<T>, Object> result = Ok.selectWithExts(eles, null, eles.size());
        Optional.ofNullable(this.selectPostprocessor()).ifPresent(it -> it.accept(eles, result));
        return result;
    }

}
