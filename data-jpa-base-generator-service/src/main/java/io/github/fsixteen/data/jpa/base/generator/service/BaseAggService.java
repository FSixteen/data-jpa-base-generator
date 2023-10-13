package io.github.fsixteen.data.jpa.base.generator.service;

import java.util.Date;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.beans.groups.GroupEntity;
import io.github.fsixteen.data.jpa.base.generator.constants.GroupDateTimeUnit;
import io.github.fsixteen.data.jpa.base.generator.constants.GroupResponseType;

/**
 * 通用Service聚合处理类.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public interface BaseAggService {

    static final Logger log = LoggerFactory.getLogger(BaseAggService.class);

    /**
     * 通用分组.
     *
     * @param request      见{@link javax.servlet.http.HttpServletRequest}
     * @param response     见{@link javax.servlet.http.HttpServletResponse}
     * @param clazz        请求实体类
     * @param groupColumns 分组字段
     * @return Object
     */
    default Object group(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, String... groupColumns) {
        return this.group(request, response, GroupResponseType.AUTO, clazz, null, groupColumns);
    }

    /**
     * 通用分组.
     *
     * @param request      见{@link javax.servlet.http.HttpServletRequest}
     * @param response     见{@link javax.servlet.http.HttpServletResponse}
     * @param clazz        请求实体类
     * @param obj          请求实体
     * @param groupColumns 分组字段
     * @return Object
     */
    default Object group(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, Object obj, String... groupColumns) {
        return this.group(request, response, GroupResponseType.AUTO, clazz, obj, groupColumns);
    }

    /**
     * 通用分组.
     *
     * @param request      见{@link javax.servlet.http.HttpServletRequest}
     * @param response     见{@link javax.servlet.http.HttpServletResponse}
     * @param type         数据应答类型
     * @param clazz        请求实体类
     * @param groupColumns 分组字段
     * @return Object
     */
    default Object group(HttpServletRequest request, HttpServletResponse response, @NotNull GroupResponseType type, Class<?> clazz, String... groupColumns) {
        return this.group(request, response, type, clazz, null, groupColumns);
    }

    /**
     * 通用分组.
     *
     * @param request      见{@link javax.servlet.http.HttpServletRequest}
     * @param response     见{@link javax.servlet.http.HttpServletResponse}
     * @param type         数据应答类型
     * @param clazz        请求实体类
     * @param obj          请求实体
     * @param groupColumns 分组字段
     * @return Object
     */
    public Object group(HttpServletRequest request, HttpServletResponse response, @NotNull GroupResponseType type, Class<?> clazz, Object obj,
        String... groupColumns);

    /**
     * 通用绘图分组.
     *
     * @param <T>          条件实体类
     * @param request      见{@link javax.servlet.http.HttpServletRequest}
     * @param response     见{@link javax.servlet.http.HttpServletResponse}
     * @param clazz        请求实体类
     * @param type         数据应答类型
     * @param unit         时间单位
     * @param st           开始时间(含)
     * @param et           结束时间(含)
     * @param valueFuns    值获取函数(数组)
     * @param groupColumns 分组字段
     * @return Object
     */
    default <T> Object dateRangeGroup(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, @NotNull GroupResponseType type,
        GroupDateTimeUnit unit, Date st, Date et, Function<GroupEntity, Object>[] valueFuns, String... groupColumns) {
        return this.dateRangeGroup(request, response, clazz, null, type, (ele) -> unit, (ele) -> st, (ele) -> et, GroupEntity::getKey, valueFuns, groupColumns);
    }

    /**
     * 通用绘图分组.
     *
     * @param <T>          条件实体类
     * @param request      见{@link javax.servlet.http.HttpServletRequest}
     * @param response     见{@link javax.servlet.http.HttpServletResponse}
     * @param clazz        请求实体类
     * @param type         数据应答类型
     * @param unit         时间单位
     * @param st           开始时间(含)
     * @param et           结束时间(含)
     * @param valueFuns    值获取函数(数组)
     * @param groupColumns 分组字段
     * @return Object
     */
    default <T> Object dateRangeGroup(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, @NotNull GroupResponseType type,
        Function<T, GroupDateTimeUnit> unit, Function<T, Date> st, Function<T, Date> et, Function<GroupEntity, Object>[] valueFuns, String... groupColumns) {
        return this.dateRangeGroup(request, response, clazz, null, type, unit, st, et, GroupEntity::getKey, valueFuns, groupColumns);
    }

    /**
     * 通用绘图分组.
     *
     * @param <T>          条件实体类
     * @param request      见{@link javax.servlet.http.HttpServletRequest}
     * @param response     见{@link javax.servlet.http.HttpServletResponse}
     * @param clazz        请求实体类
     * @param type         数据应答类型
     * @param unit         时间单位
     * @param st           开始时间(含)
     * @param et           结束时间(含)
     * @param labelFun     标题获取函数
     * @param valueFuns    值获取函数(数组)
     * @param groupColumns 分组字段
     * @return Object
     */
    default <T> Object dateRangeGroup(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, @NotNull GroupResponseType type,
        GroupDateTimeUnit unit, Date st, Date et, Function<GroupEntity, Object> labelFun, Function<GroupEntity, Object>[] valueFuns, String... groupColumns) {
        return this.dateRangeGroup(request, response, clazz, null, type, (ele) -> unit, (ele) -> st, (ele) -> et, labelFun, valueFuns, groupColumns);
    }

    /**
     * 通用绘图分组.
     *
     * @param <T>          条件实体类
     * @param request      见{@link javax.servlet.http.HttpServletRequest}
     * @param response     见{@link javax.servlet.http.HttpServletResponse}
     * @param clazz        请求实体类
     * @param type         数据应答类型
     * @param unit         时间单位
     * @param st           开始时间(含)
     * @param et           结束时间(含)
     * @param labelFun     标题获取函数
     * @param valueFuns    值获取函数(数组)
     * @param groupColumns 分组字段
     * @return Object
     */
    default <T> Object dateRangeGroup(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, @NotNull GroupResponseType type,
        Function<T, GroupDateTimeUnit> unit, Function<T, Date> st, Function<T, Date> et, Function<GroupEntity, Object> labelFun,
        Function<GroupEntity, Object>[] valueFuns, String... groupColumns) {
        return this.dateRangeGroup(request, response, clazz, null, type, unit, st, et, labelFun, valueFuns, groupColumns);
    }

    /**
     * 通用绘图分组.
     *
     * @param <T>          条件实体类
     * @param request      见{@link javax.servlet.http.HttpServletRequest}
     * @param response     见{@link javax.servlet.http.HttpServletResponse}
     * @param clazz        请求实体类
     * @param obj          请求实体
     * @param type         数据应答类型
     * @param unit         时间单位
     * @param st           开始时间(含)
     * @param et           结束时间(含)
     * @param valueFuns    值获取函数(数组)
     * @param groupColumns 分组字段
     * @return Object
     */
    default <T> Object dateRangeGroup(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, T obj, @NotNull GroupResponseType type,
        GroupDateTimeUnit unit, Date st, Date et, Function<GroupEntity, Object>[] valueFuns, String... groupColumns) {
        return this.dateRangeGroup(request, response, clazz, obj, type, (ele) -> unit, (ele) -> st, (ele) -> et, GroupEntity::getKey, valueFuns, groupColumns);
    }

    /**
     * 通用绘图分组.
     *
     * @param <T>          条件实体类
     * @param request      见{@link javax.servlet.http.HttpServletRequest}
     * @param response     见{@link javax.servlet.http.HttpServletResponse}
     * @param clazz        请求实体类
     * @param obj          请求实体
     * @param type         数据应答类型
     * @param unit         时间单位
     * @param st           开始时间(含)
     * @param et           结束时间(含)
     * @param labelFun     标题获取函数
     * @param valueFuns    值获取函数(数组)
     * @param groupColumns 分组字段
     * @return Object
     */
    default <T> Object dateRangeGroup(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, T obj, @NotNull GroupResponseType type,
        GroupDateTimeUnit unit, Date st, Date et, Function<GroupEntity, Object> labelFun, Function<GroupEntity, Object>[] valueFuns, String... groupColumns) {
        return this.dateRangeGroup(request, response, clazz, obj, type, (ele) -> unit, (ele) -> st, (ele) -> et, labelFun, valueFuns, groupColumns);
    }

    /**
     * 通用绘图分组.
     *
     * @param <T>          条件实体类
     * @param request      见{@link javax.servlet.http.HttpServletRequest}
     * @param response     见{@link javax.servlet.http.HttpServletResponse}
     * @param clazz        请求实体类
     * @param obj          请求实体
     * @param type         数据应答类型
     * @param unit         时间单位
     * @param st           开始时间(含)
     * @param et           结束时间(含)
     * @param labelFun     标题获取函数
     * @param valueFuns    值获取函数(数组)
     * @param groupColumns 分组字段
     * @return Object
     */
    public <T> Object dateRangeGroup(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, T obj, @NotNull GroupResponseType type,
        Function<T, GroupDateTimeUnit> unit, Function<T, Date> st, Function<T, Date> et, Function<GroupEntity, Object> labelFun,
        Function<GroupEntity, Object>[] valueFuns, String... groupColumns);

}
