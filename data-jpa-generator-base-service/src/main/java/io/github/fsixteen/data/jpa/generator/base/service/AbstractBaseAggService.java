package io.github.fsixteen.data.jpa.generator.base.service;

import static io.github.fsixteen.data.jpa.generator.utils.DateTimeUtils.getTimeArrayByTimeRange;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import io.github.fsixteen.data.jpa.base.generator.plugins.cache.CollectionCache;
import io.github.fsixteen.data.jpa.base.generator.plugins.collections.AnnotationCollection;
import io.github.fsixteen.data.jpa.base.generator.plugins.collections.ComputerCollection;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;
import io.github.fsixteen.data.jpa.generator.beans.groups.GroupEntity;
import io.github.fsixteen.data.jpa.generator.constants.GroupDateTimeUnit;
import io.github.fsixteen.data.jpa.generator.constants.GroupResponseType;
import io.github.fsixteen.data.jpa.generator.utils.GroupColumnUtils;
import io.github.fsixteen.data.jpa.generator.utils.GroupColumnUtils.Column;

/**
 * 通用Service聚合处理类.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public abstract class AbstractBaseAggService implements BaseAggService {

    @Resource
    private EntityManager em;

    private void setSelectAndGrouping(CriteriaBuilder cb, CriteriaQuery<GroupEntity> query, Root<?> root, String... groupColumn) {
        List<Selection<?>> selections = new LinkedList<>();
        List<Expression<?>> grouping = new LinkedList<>();
        for (String column : groupColumn) {
            Column arg = GroupColumnUtils.of(column);
            arg.toSelection(root, cb).ifPresent(selections::add);
            arg.toExpression(root, cb).ifPresent(grouping::add);
        }
        selections.add(cb.count(cb.literal(1)));
        query.multiselect(selections);
        query.groupBy(grouping);
    }

    private void setQueryWhere(Object obj, CriteriaBuilder cb, CriteriaQuery<GroupEntity> query, Root<?> root) {
        if (Objects.nonNull(obj)) {
            AnnotationCollection computer = CollectionCache.getAnnotationCollection(obj.getClass());
            Predicate[] list = ComputerCollection.Builder.of().withAnnotationCollection(computer).withArgs(obj).withSpecification(root, query, cb)
                .build(BuilderType.SELECTED).getPredicateArray(cb);
            query.where(cb.and(list));
        }
    }

    private CriteriaQuery<GroupEntity> createCriteriaQuery(Class<?> clazz, Object obj, String... groupColumns) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<GroupEntity> query = cb.createQuery(GroupEntity.class);
        Root<?> root = query.from(clazz);
        this.setSelectAndGrouping(cb, query, root, groupColumns);
        this.setQueryWhere(obj, cb, query, root);
        return query;
    }

    @Override
    public Object group(HttpServletRequest request, HttpServletResponse response, @NotNull GroupResponseType type, Class<?> clazz, Object obj,
        String... groupColumns) {
        CriteriaQuery<GroupEntity> query = this.createCriteriaQuery(clazz, obj, groupColumns);
        List<GroupEntity> rs = this.em.createQuery(query).getResultList();
        switch (type) {
            case AUTO:
            case ARRAY:
            case LIST:
            case LISTMAP:
                return rs;
            case LISTARRAY:
            case ECHARTS:
                return rs.stream().map(GroupEntity::toArray).collect(Collectors.toList());
            case MAP:
            case NESTED:
            default:
                return rs.stream().collect(GroupEntity.toMap(groupColumns.length));
        }
    }

    @Override
    public <T> Object dateRangeGroup(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, T obj, @NotNull GroupResponseType type,
        Function<T, GroupDateTimeUnit> unit, Function<T, Date> st, Function<T, Date> et, Function<GroupEntity, Object> labelFun,
        Function<GroupEntity, Object>[] valueFuns, String... groupColumns) {
        final String[] timeRange = getTimeArrayByTimeRange(st.apply(obj), et.apply(obj), unit.apply(obj).getLocal(), unit.apply(obj).getOffset());
        CriteriaQuery<GroupEntity> query = this.createCriteriaQuery(clazz, obj, groupColumns);
        List<GroupEntity> rs = this.em.createQuery(query).getResultList();

        switch (type) {
            case ECHARTS: {
                Object[][] valueRange = new Object[valueFuns.length][timeRange.length];
                Arrays.asList(valueRange).forEach(it -> Arrays.fill(it, 0L));
                Map<Object, GroupEntity> kvs = rs.stream().collect(Collectors.toMap(labelFun::apply, it -> it));
                for (int index = 0; index < timeRange.length; index++) {
                    for (int findex = 0; findex < valueFuns.length; findex++) {
                        if (kvs.containsKey(timeRange[index])) {
                            valueRange[findex][index] = valueFuns[findex].apply(kvs.get(timeRange[index]));
                            valueRange[findex][index] = valueFuns[findex].apply(kvs.get(timeRange[index]));
                        }
                    }
                }
                return new GroupEntity(timeRange, 1 == valueFuns.length ? valueRange[0] : valueRange).getTuple();
            }
            default:
                return rs;
        }
    }

}
