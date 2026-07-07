package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.util.Objects;
import java.util.function.Predicate;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.PredicateRef;

/**
 * 编译后的条件过滤扩展引用.
 *
 * <p>
 * `Compare` / `Membership` 等注解会用它表达“集合元素过滤”或“运行时值过滤”扩展.
 * 注解层统一只暴露 {@link PredicateRef}, compiled 层则通过该对象屏蔽默认值与空值细节.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
final class CompiledPredicateFilterSpec {

    private final Class<?> predicateClass;

    private CompiledPredicateFilterSpec(final Class<?> predicateClass) {
        this.predicateClass = predicateClass;
    }

    /**
     * 将注解层 {@link PredicateRef} 解析为 compiled 层过滤扩展引用.
     */
    static CompiledPredicateFilterSpec of(final PredicateRef predicateRef) {
        if (Objects.isNull(predicateRef)) {
            return new CompiledPredicateFilterSpec(null);
        }
        Class<?> predicateClass = Void.class.equals(predicateRef.predicateClass()) ? null : predicateRef.predicateClass();
        return new CompiledPredicateFilterSpec(predicateClass);
    }

    /**
     * 返回声明的过滤 predicate 类型.
     */
    Class<?> getPredicateClass() {
        return this.predicateClass;
    }

    /**
     * 解析并实例化过滤 predicate.
     */
    Predicate<Object> resolve() {
        if (null != this.predicateClass) {
            try {
                return CompiledReferenceResolvers.predicateByClass(this.predicateClass);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Predicate class not found: " + this.predicateClass.getName(), e);
            }
        }
        return null;
    }

}
