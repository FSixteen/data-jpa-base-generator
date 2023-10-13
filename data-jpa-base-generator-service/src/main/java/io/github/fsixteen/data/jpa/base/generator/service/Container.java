package io.github.fsixteen.data.jpa.base.generator.service;

import java.util.Objects;

/**
 * 容器.<br>
 *
 * @author FSixteen
 * @since 1.0.1
 */
class Container<T> {
    private T sort;

    protected Container() {
    }

    protected Container(T sort) {
        this.sort = sort;
    }

    public T getSort() {
        return sort;
    }

    public void setSort(T sort) {
        this.sort = sort;
    }

    public boolean isNull() {
        return Objects.isNull(this.getSort());
    }
}