package io.github.fsixteen.data.jpa.base.generator.plugins.cache;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Between;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.EndWith;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal;
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
import io.github.fsixteen.data.jpa.base.generator.plugins.ComparableBuilderPlugin.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.InBuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.InBuilderPlugin.InType;
import io.github.fsixteen.data.jpa.base.generator.plugins.InTableBuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.LikeBuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.LikeBuilderPlugin.LikeType;
import io.github.fsixteen.data.jpa.base.generator.plugins.NumberBuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.NumberBuilderPlugin.NumberType;
import io.github.fsixteen.data.jpa.base.generator.plugins.SplitInBuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.SplitNotInBuilderPlugin;

/**
 * @author FSixteen
 * @since V1.0.0
 */
public final class PluginsCache {

    private static final Map<Class<? extends Annotation>, BuilderPlugin<? extends Annotation>> PLUGINS = new ConcurrentHashMap<>();

    static {
        PLUGINS.put(Equal.class, new ComparableBuilderPlugin(ComparableType.EQ));
        PLUGINS.put(Between.class, new BetweenBuilderPlugin());

        PLUGINS.put(Lt.class, new NumberBuilderPlugin(NumberType.LT));
        PLUGINS.put(Lte.class, new NumberBuilderPlugin(NumberType.LTE));
        PLUGINS.put(Gt.class, new NumberBuilderPlugin(NumberType.GT));
        PLUGINS.put(Gte.class, new NumberBuilderPlugin(NumberType.GTE));

        PLUGINS.put(LessThan.class, new ComparableBuilderPlugin(ComparableType.LT));
        PLUGINS.put(LessThanOrEqualTo.class, new ComparableBuilderPlugin(ComparableType.LTE));
        PLUGINS.put(GreaterThan.class, new ComparableBuilderPlugin(ComparableType.GT));
        PLUGINS.put(GreaterThanOrEqualTo.class, new ComparableBuilderPlugin(ComparableType.GTE));

        PLUGINS.put(Like.class, new LikeBuilderPlugin(LikeType.CENNTER));
        PLUGINS.put(LeftLike.class, new LikeBuilderPlugin(LikeType.LEFT));
        PLUGINS.put(RightLike.class, new LikeBuilderPlugin(LikeType.RIGHT));
        PLUGINS.put(StartWith.class, new LikeBuilderPlugin(LikeType.LEFT));
        PLUGINS.put(EndWith.class, new LikeBuilderPlugin(LikeType.RIGHT));

        PLUGINS.put(In.class, new InBuilderPlugin<In>(InType.IN));
        PLUGINS.put(NotIn.class, new InBuilderPlugin<NotIn>(InType.IN));

        PLUGINS.put(SplitIn.class, new SplitInBuilderPlugin());
        PLUGINS.put(SplitNotIn.class, new SplitNotInBuilderPlugin());

        PLUGINS.put(InTable.class, new InTableBuilderPlugin());

        PLUGINS.put(Unique.class, new ComparableBuilderPlugin(ComparableType.EQ));
    }

    public static boolean containsKey(Class<? extends Annotation> type) {
        return PLUGINS.containsKey(type) && Objects.nonNull(PLUGINS.get(type));
    }

    public static void register(Class<? extends Annotation> type, BuilderPlugin<? extends Annotation> obj) {
        PLUGINS.put(type, obj);
    }

    public static BuilderPlugin<? extends Annotation> reference(Class<? extends Annotation> type) {
        return PLUGINS.get(type);
    }

}
