package io.github.fsixteen.data.jpa.generator.base.service;

import java.util.Date;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.generator.beans.groups.GroupEntity;
import io.github.fsixteen.data.jpa.generator.constants.GroupDateTimeUnit;
import io.github.fsixteen.data.jpa.generator.constants.GroupResponseType;

/**
 * 通用Service聚合处理类.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
public interface BaseAggService {
    static final Logger log = LoggerFactory.getLogger(BaseAggService.class);

    /**
     * 通用分组.
     * 
     * @param request
     * @param response
     * @param clazz
     * @param groupColumns
     * @return Object
     */
    default Object group(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, String... groupColumns) {
        return this.group(request, response, GroupResponseType.AUTO, clazz, null, groupColumns);
    }

    /**
     * 通用分组.
     * 
     * @param request
     * @param response
     * @param clazz
     * @param obj
     * @param groupColumns
     * @return Object
     */
    default Object group(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, Object obj, String... groupColumns) {
        return this.group(request, response, GroupResponseType.AUTO, clazz, obj, groupColumns);
    }

    /**
     * 通用分组.
     * 
     * @param request
     * @param response
     * @param type
     * @param clazz
     * @param groupColumns
     * @return Object
     */
    default Object group(HttpServletRequest request, HttpServletResponse response, @NotNull GroupResponseType type, Class<?> clazz, String... groupColumns) {
        return this.group(request, response, type, clazz, null, groupColumns);
    }

    /**
     * 通用分组.
     *
     * @param request
     * @param response
     * @param type
     * @param clazz
     * @param obj
     * @param groupColumns
     * @return
     */
    public Object group(HttpServletRequest request, HttpServletResponse response, @NotNull GroupResponseType type, Class<?> clazz, Object obj,
            String... groupColumns);

    /**
     * 通用绘图分组.
     * 
     * @param <T>
     * @param request
     * @param response
     * @param clazz
     * @param unit
     * @param st
     * @param et
     * @param valueFuns
     * @param groupColumns
     * @return Object
     */
    default <T> Object dateRangeGroup(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, GroupDateTimeUnit unit, Date st, Date et,
            Function<GroupEntity, Object>[] valueFuns, String... groupColumns) {
        return this.dateRangeGroup(request, response, clazz, null, (ele) -> unit, (ele) -> st, (ele) -> et, GroupEntity::getKey, valueFuns, groupColumns);
    }

    /**
     * 通用绘图分组.
     * 
     * @param <T>
     * @param request
     * @param response
     * @param clazz
     * @param unit
     * @param st
     * @param et
     * @param valueFuns
     * @param groupColumns
     * @return Object
     */
    default <T> Object dateRangeGroup(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, Function<T, GroupDateTimeUnit> unit,
            Function<T, Date> st, Function<T, Date> et, Function<GroupEntity, Object>[] valueFuns, String... groupColumns) {
        return this.dateRangeGroup(request, response, clazz, null, unit, st, et, GroupEntity::getKey, valueFuns, groupColumns);
    }

    /**
     * 通用绘图分组.
     * 
     * @param <T>
     * @param request
     * @param response
     * @param clazz
     * @param unit
     * @param st
     * @param et
     * @param labelFun
     * @param valueFuns
     * @param groupColumns
     * @return Object
     */
    default <T> Object dateRangeGroup(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, GroupDateTimeUnit unit, Date st, Date et,
            Function<GroupEntity, Object> labelFun, Function<GroupEntity, Object>[] valueFuns, String... groupColumns) {
        return this.dateRangeGroup(request, response, clazz, null, (ele) -> unit, (ele) -> st, (ele) -> et, labelFun, valueFuns, groupColumns);
    }

    /**
     * 通用绘图分组.
     * 
     * @param <T>
     * @param request
     * @param response
     * @param clazz
     * @param unit
     * @param st
     * @param et
     * @param labelFun
     * @param valueFuns
     * @param groupColumns
     * @return Object
     */
    default <T> Object dateRangeGroup(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, Function<T, GroupDateTimeUnit> unit,
            Function<T, Date> st, Function<T, Date> et, Function<GroupEntity, Object> labelFun, Function<GroupEntity, Object>[] valueFuns,
            String... groupColumns) {
        return this.dateRangeGroup(request, response, clazz, null, unit, st, et, labelFun, valueFuns, groupColumns);
    }

    /**
     * 通用绘图分组.
     * 
     * @param <T>
     * @param request
     * @param response
     * @param clazz
     * @param obj
     * @param unit
     * @param st
     * @param et
     * @param valueFuns
     * @param groupColumns
     * @return Object
     */
    default <T> Object dateRangeGroup(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, T obj, GroupDateTimeUnit unit, Date st, Date et,
            Function<GroupEntity, Object>[] valueFuns, String... groupColumns) {
        return this.dateRangeGroup(request, response, clazz, obj, (ele) -> unit, (ele) -> st, (ele) -> et, GroupEntity::getKey, valueFuns, groupColumns);
    }

    /**
     * 通用绘图分组.
     * 
     * @param <T>
     * @param request
     * @param response
     * @param clazz
     * @param obj
     * @param unit
     * @param st
     * @param et
     * @param labelFun
     * @param valueFuns
     * @param groupColumns
     * @return Object
     */
    default <T> Object dateRangeGroup(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, T obj, GroupDateTimeUnit unit, Date st, Date et,
            Function<GroupEntity, Object> labelFun, Function<GroupEntity, Object>[] valueFuns, String... groupColumns) {
        return this.dateRangeGroup(request, response, clazz, obj, (ele) -> unit, (ele) -> st, (ele) -> et, labelFun, valueFuns, groupColumns);
    }

    /**
     * 通用绘图分组.
     * 
     * @param <T>
     * @param request
     * @param response
     * @param clazz
     * @param obj
     * @param unit
     * @param st
     * @param et
     * @param labelFun
     * @param valueFuns
     * @param groupColumns
     * @return Object
     */
    public <T> Object dateRangeGroup(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, T obj, Function<T, GroupDateTimeUnit> unit,
            Function<T, Date> st, Function<T, Date> et, Function<GroupEntity, Object> labelFun, Function<GroupEntity, Object>[] valueFuns,
            String... groupColumns);

}
