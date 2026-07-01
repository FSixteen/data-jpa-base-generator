package io.github.fsixteen.data.jpa.base.generator.plugins.collections;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type;

/**
 * `@GroupComputerType` 元数据的只读运行时视图。
 *
 * <p>
 * 外部公开入口仍然通过 `AnnotationCollection/ComputerCollection` 暴露分组合并规则，
 * 但内部不再让 `AnnotationCollection` 同时承担“注解编译结果容器”和“分组元数据注册表”两种职责。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
final class GroupComputerRegistry {

    private static final GroupComputerRegistry EMPTY = new GroupComputerRegistry(new GroupComputerType[0],
        new ConcurrentHashMap<String, Map<String, GroupComputerType>>());

    private final GroupComputerType[] declarations;

    private final Map<String, Map<String, GroupComputerType>> groupsByScope;

    private GroupComputerRegistry(final GroupComputerType[] declarations, final Map<String, Map<String, GroupComputerType>> groupsByScope) {
        this.declarations = null == declarations ? new GroupComputerType[0] : declarations.clone();
        this.groupsByScope = groupsByScope;
    }

    static GroupComputerRegistry empty() {
        return EMPTY;
    }

    static GroupComputerRegistry of(final Class<?> clazz, final Logger log) {
        GroupComputerType[] declarations = clazz.getAnnotationsByType(GroupComputerType.class);
        Map<String, Map<String, GroupComputerType>> groupsByScope = new ConcurrentHashMap<String, Map<String, GroupComputerType>>();
        for (GroupComputerType declaration : declarations) {
            for (String scope : declaration.scope()) {
                Map<String, GroupComputerType> scoped = groupsByScope.computeIfAbsent(scope, key -> new ConcurrentHashMap<String, GroupComputerType>());
                if (scoped.containsKey(declaration.value())) {
                    log.warn("%s, @GroupComputerType 重复出现.", clazz.getName());
                }
                scoped.put(declaration.value(), declaration);
            }
        }
        return new GroupComputerRegistry(declarations, groupsByScope);
    }

    Type type(final String scope, final String value) {
        return Optional.ofNullable(group(scope, value)).map(GroupComputerType::type).orElse(Type.AND);
    }

    Map<String, GroupComputerType> groupsByScope(final String scope) {
        return this.groupsByScope.get(scope);
    }

    GroupComputerType group(final String scope, final String value) {
        return this.groupsByScope.getOrDefault(scope, Collections.emptyMap()).get(value);
    }

    GroupComputerType[] declarationsForScope(final String scope) {
        GroupComputerType[] filtered = new GroupComputerType[this.declarations.length];
        int offset = 0;
        for (GroupComputerType declaration : this.declarations) {
            if (containsScope(declaration, scope)) {
                filtered[offset++] = declaration;
            }
        }
        return Arrays.copyOf(filtered, offset);
    }

    GroupComputerType[] declarations() {
        return this.declarations.clone();
    }

    private static boolean containsScope(final GroupComputerType declaration, final String scope) {
        for (String candidate : declaration.scope()) {
            if (candidate.equals(scope)) {
                return true;
            }
        }
        return false;
    }

}
