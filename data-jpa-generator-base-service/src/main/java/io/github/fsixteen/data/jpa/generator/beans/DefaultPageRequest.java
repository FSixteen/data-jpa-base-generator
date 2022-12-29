package io.github.fsixteen.data.jpa.generator.beans;

import io.github.fsixteen.data.jpa.generator.base.entities.Entity;

/**
 * @author FSixteen
 * @since V1.0.0
 */
public class DefaultPageRequest implements BasePageRequest, Entity {
    private static final long serialVersionUID = 1L;

    private int page;

    private int size;

    public DefaultPageRequest() {
    }

    public DefaultPageRequest(int page, int size) {
        super();
        this.page = page;
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
