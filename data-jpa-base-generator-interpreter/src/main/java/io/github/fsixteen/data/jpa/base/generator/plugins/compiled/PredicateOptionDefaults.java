package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;

/**
 * canonical 谓词默认公共选项。
 *
 * <p>
 * 顶层查询注解默认仍应落在 {@code default} scope / group 上，但该默认值不应该在每个注解上重复声明。
 * 因此这里集中维护 compiled 主链路的公共默认值；分支或嵌套场景若显式提供
 * {@code @PredicateOptions()}，仍然可以继续表达“继承父级”的语义。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
final class PredicateOptionDefaults {

    private static final String[] DEFAULT_SCOPE = { Constant.DEFAULT };

    private static final GroupInfo[] DEFAULT_GROUPS = { defaultGroupInfo() };

    private PredicateOptionDefaults() {
    }

    /**
     * 返回顶层 compiled 注解统一使用的默认公共选项。
     */
    static PredicateOptionsSpec defaults() {
        return PredicateOptionsSpec.merge(defaultScope(), defaultGroups(), false, false, true, true, true, true, null);
    }

    /**
     * 返回顶层默认 scope。
     */
    static String[] defaultScope() {
        return DEFAULT_SCOPE.clone();
    }

    /**
     * 返回顶层默认 group。
     */
    static GroupInfo[] defaultGroups() {
        return DEFAULT_GROUPS.clone();
    }

    /**
     * 构造默认 {@link GroupInfo} 注解实例。
     */
    private static GroupInfo defaultGroupInfo() {
        InvocationHandler handler = new InvocationHandler() {

            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) {
                String methodName = method.getName();
                if ("value".equals(methodName)) {
                    return Constant.DEFAULT;
                }
                if ("order".equals(methodName)) {
                    return Integer.valueOf(0);
                }
                if ("annotationType".equals(methodName)) {
                    return GroupInfo.class;
                }
                if ("toString".equals(methodName)) {
                    return "@GroupInfo(value=default, order=0)";
                }
                if ("hashCode".equals(methodName)) {
                    return Integer.valueOf(127 * "value".hashCode() ^ Constant.DEFAULT.hashCode() ^ 127 * "order".hashCode() ^ Integer.valueOf(0).hashCode());
                }
                if ("equals".equals(methodName)) {
                    Object otherObject = null == args || 0 == args.length ? null : args[0];
                    if (!(otherObject instanceof GroupInfo)) {
                        return Boolean.FALSE;
                    }
                    GroupInfo other = GroupInfo.class.cast(otherObject);
                    return Boolean.valueOf(Constant.DEFAULT.equals(other.value()) && 0 == other.order());
                }
                throw new UnsupportedOperationException(method.toString());
            }

        };
        return GroupInfo.class.cast(Proxy.newProxyInstance(PredicateOptionDefaults.class.getClassLoader(), new Class<?>[] { GroupInfo.class }, handler));
    }

}
