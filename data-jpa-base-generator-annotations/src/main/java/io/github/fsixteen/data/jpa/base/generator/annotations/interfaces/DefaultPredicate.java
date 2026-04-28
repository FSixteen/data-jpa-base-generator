package io.github.fsixteen.data.jpa.base.generator.annotations.interfaces;

import java.util.function.Predicate;

/**
 * 默认判断处理器.<br>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public class DefaultPredicate implements Predicate<Object> {

    @Override
    public boolean test(Object t) {
        return true;
    }

}
