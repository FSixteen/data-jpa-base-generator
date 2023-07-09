package io.github.fsixteen.data.jpa.base.generator.plugins.cache;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import io.github.fsixteen.data.jpa.base.generator.annotations.Existed;
import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Between;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.EndWith;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.FilterIn;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.FilterNotIn;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.GreaterThan;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.GreaterThanOrEqualTo;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Gt;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Gte;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.In;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.InTable;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.LeftLike;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.LessThan;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.LessThanOrEqualTo;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Like;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Lt;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Lte;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NotIn;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.RightLike;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SplitIn;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SplitNotIn;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.StartWith;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Unique;
import io.github.fsixteen.data.jpa.base.generator.plugins.BetweenBuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.BuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.ComparableBuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.FilterInBuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.InBuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.InTableBuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.LikeBuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.NumberBuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.SplitInBuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.SplitNotInBuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;

/**
 * 注解及注解对应解析器缓存.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public final class PluginsCache {

    private static final Map<Class<? extends Annotation>, BuilderPlugin<? extends Annotation>> PLUGINS = new ConcurrentHashMap<>();

    static {
        PLUGINS.put(Existed.class, new ComparableBuilderPlugin(ComparableType.EQ));
        PLUGINS.put(Selectable.class, new ComparableBuilderPlugin(ComparableType.EQ));

        PLUGINS.put(Equal.class, new ComparableBuilderPlugin(ComparableType.EQ));
        PLUGINS.put(Between.class, new BetweenBuilderPlugin());

        PLUGINS.put(Lt.class, new NumberBuilderPlugin(ComparableType.LT));
        PLUGINS.put(Lte.class, new NumberBuilderPlugin(ComparableType.LTE));
        PLUGINS.put(Gt.class, new NumberBuilderPlugin(ComparableType.GT));
        PLUGINS.put(Gte.class, new NumberBuilderPlugin(ComparableType.GTE));

        PLUGINS.put(LessThan.class, new ComparableBuilderPlugin(ComparableType.LT));
        PLUGINS.put(LessThanOrEqualTo.class, new ComparableBuilderPlugin(ComparableType.LTE));
        PLUGINS.put(GreaterThan.class, new ComparableBuilderPlugin(ComparableType.GT));
        PLUGINS.put(GreaterThanOrEqualTo.class, new ComparableBuilderPlugin(ComparableType.GTE));

        PLUGINS.put(Like.class, new LikeBuilderPlugin(ComparableType.CONTAINS));
        PLUGINS.put(LeftLike.class, new LikeBuilderPlugin(ComparableType.LEFT));
        PLUGINS.put(RightLike.class, new LikeBuilderPlugin(ComparableType.RIGHT));
        PLUGINS.put(StartWith.class, new LikeBuilderPlugin(ComparableType.LEFT));
        PLUGINS.put(EndWith.class, new LikeBuilderPlugin(ComparableType.RIGHT));

        PLUGINS.put(In.class, new InBuilderPlugin<In>(ComparableType.IN));
        PLUGINS.put(NotIn.class, new InBuilderPlugin<NotIn>(ComparableType.NOT_IN));
        PLUGINS.put(FilterIn.class, new FilterInBuilderPlugin<FilterIn>(ComparableType.IN));
        PLUGINS.put(FilterNotIn.class, new FilterInBuilderPlugin<FilterNotIn>(ComparableType.NOT_IN));

        PLUGINS.put(SplitIn.class, new SplitInBuilderPlugin());
        PLUGINS.put(SplitNotIn.class, new SplitNotInBuilderPlugin());

        PLUGINS.put(InTable.class, new InTableBuilderPlugin());

        PLUGINS.put(Unique.class, new ComparableBuilderPlugin(ComparableType.EQ));
    }

    /**
     * 判断注解是否存在注解解析器.<br>
     * 
     * @param type 注解类
     * @return boolean
     */
    public static boolean containsKey(Class<? extends Annotation> type) {
        return PLUGINS.containsKey(type) && Objects.nonNull(PLUGINS.get(type));
    }

    /**
     * 注册注解解析器.<br>
     * 
     * @param type 注解类
     * @param obj  注解解析器
     */
    public static void register(Class<? extends Annotation> type, BuilderPlugin<? extends Annotation> obj) {
        PLUGINS.put(type, obj);
    }

    /**
     * 引用注解解析器.<br>
     * 
     * @param type 注解类
     * @return BuilderPlugin&lt;? extends Annotation>&gt;
     * @throws ClassNotFoundException If the class cannot be located
     */
    public static BuilderPlugin<? extends Annotation> reference(String type) throws ClassNotFoundException {
        return PLUGINS.get(Class.forName(type));
    }

    /**
     * 引用注解解析器.<br>
     * 
     * @param type 注解类
     * @return BuilderPlugin&lt;? extends Annotation>&gt;
     */
    public static BuilderPlugin<? extends Annotation> reference(Class<? extends Annotation> type) {
        return PLUGINS.get(type);
    }

}
