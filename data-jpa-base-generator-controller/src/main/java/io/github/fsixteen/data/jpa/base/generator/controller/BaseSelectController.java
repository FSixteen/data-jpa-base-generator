package io.github.fsixteen.data.jpa.base.generator.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.github.fsixteen.common.structure.Response;
import io.github.fsixteen.common.structure.extend.Err;
import io.github.fsixteen.common.structure.extend.Ok;
import io.github.fsixteen.data.jpa.base.generator.entities.Entity;
import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;
import io.github.fsixteen.data.jpa.base.generator.groups.SelectGroup;
import io.github.fsixteen.data.jpa.base.generator.query.BasePageRequest;
import io.github.fsixteen.data.jpa.base.generator.service.BaseSelectService;
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
     * 单一详细查询逻辑方法.<br>
     * <p>
     * 此方法仅封装查询逻辑, 不包含任何HTTP映射注解. 实现类应根据自身ID类型选择合适的端点暴露方式:
     * </p>
     * <p>
     * <b>ID为简单类型(Long/String等)时, 使用 @PathVariable:</b>
     * </p>
     * 
     * <pre>
     * 
     * &#64;Operation(summary = "单一详细查询")
     * &#64;PostMapping("select/{id}")
     * public Response&lt;T, Object&gt; select(HttpServletRequest request, HttpServletResponse response,
     *     &#64;NotNull(message = "请指定查询内容") &#64;PathVariable("id") final ID id) {
     *     return this.findById(request, response, id);
     * }
     * </pre>
     * <p>
     * <b>ID为复杂类型(复合主键等)时, 使用 @RequestBody:</b>
     * </p>
     * 
     * <pre>
     * 
     * &#64;Operation(summary = "单一详细查询")
     * &#64;PostMapping("select/details")
     * public Response&lt;T, Object&gt; selectOne(HttpServletRequest request, HttpServletResponse response,
     *     &#64;Validated(value = { SelectGroup.class }) &#64;RequestBody final ID id) {
     *     return this.findById(request, response, id);
     * }
     * </pre>
     * <p>
     * <b>注意:</b> 两种方式不应同时存在于同一个实现类中, 因为ID类型是确定的, 只有一种端点可用.
     * </p>
     *
     * @param request  {@link javax.servlet.http.HttpServletRequest}实例, 自动注入
     * @param response {@link javax.servlet.http.HttpServletResponse}实例, 自动注入
     * @param id       主键ID, 类型由泛型参数ID决定
     * @return Response&lt;T, Object&gt; 查询结果
     * @see #selectPostprocessor()
     */
    default Response<T, Object> findById(HttpServletRequest request, HttpServletResponse response, ID id) {
        Optional<T> eles = this.getService().findById(id);
        Response<T, Object> result = eles.isPresent() ? Ok.selectWithExts(eles.get(), null) : Err.selectWithExts(null, null);
        if (eles.isPresent()) {
            Optional.ofNullable(this.selectPostprocessor()).ifPresent(it -> it.accept(Arrays.asList(eles.get()), result));
        }
        return result;
    }

    /**
     * 单一详细查询.<br>
     * 
     * @param request  {@link javax.servlet.http.HttpServletRequest}实例, 自动注入
     * @param response {@link javax.servlet.http.HttpServletResponse}实例, 自动注入
     * @param id       请求数据
     * @return Response&lt;List&lt;T&gt;, Object&gt;
     */
    /*
     * @Operation(summary = "单一详细查询", description = "单一详细查询")
     * @PostMapping(value = "select/details")
     * default Response<T, Object> selectOne(HttpServletRequest request,
     * HttpServletResponse response,
     * @Validated(value = { SelectGroup.class }) @NotNull(message =
     * "请指定查询内容") @RequestBody final ID id) {
     * Optional<T> eles = this.getService().findById(id);
     * Response<T, Object> result = eles.isPresent() ? Ok.selectWithExts(eles.get(),
     * null) : Err.selectWithExts(null, null);
     * if (eles.isPresent()) {
     * Optional.ofNullable(this.selectPostprocessor()).ifPresent(it ->
     * it.accept(Arrays.asList(eles.get()), result));
     * }
     * return result;
     * }
     */

    /**
     * 单一详细查询.<br>
     *
     * @param request  {@link javax.servlet.http.HttpServletRequest}实例, 自动注入
     * @param response {@link javax.servlet.http.HttpServletResponse}实例, 自动注入
     * @param id       请求数据
     * @return Response&lt;List&lt;T&gt;, Object&gt;
     */
    /*
     * @Operation(summary = "单一详细查询", description = "单一详细查询")
     * @PostMapping(value = "select/by/{id}")
     * default Response<T, Object> select(HttpServletRequest request,
     * HttpServletResponse response,
     * @Validated(value = { SelectGroup.class }) @NotNull(message =
     * "请指定查询内容") @PathVariable("id") final ID id) {
     * Optional<T> eles = this.getService().findById(id);
     * Response<T, Object> result = eles.isPresent() ? Ok.selectWithExts(eles.get(),
     * null) : Err.selectWithExts(null, null);
     * if (eles.isPresent()) {
     * Optional.ofNullable(this.selectPostprocessor()).ifPresent(it ->
     * it.accept(Arrays.asList(eles.get()), result));
     * }
     * return result;
     * }
     */

    /**
     * 分页查询.<br>
     * 
     * @param request  {@link javax.servlet.http.HttpServletRequest}实例, 自动注入
     * @param response {@link javax.servlet.http.HttpServletResponse}实例, 自动注入
     * @param data     请求实例
     * @return Response&lt;List&lt;T&gt;, Object&gt;
     */
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
    @Operation(summary = "完整查询", description = "完整查询", hidden = true)
    @PostMapping(value = "select/all")
    default Response<List<T>, Object> selectAll(HttpServletRequest request, HttpServletResponse response) {
        List<T> eles = this.getService().selectAll();
        Response<List<T>, Object> result = Ok.selectWithExts(eles, null, eles.size());
        Optional.ofNullable(this.selectPostprocessor()).ifPresent(it -> it.accept(eles, result));
        return result;
    }

}
