package io.github.fsixteen.data.jpa.generator.beans;

import io.github.fsixteen.data.jpa.generator.base.entities.Entity;

/**
 * 默认分页请求.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public class DefaultPageRequest implements BasePageRequest, Entity {
    private static final long serialVersionUID = 1L;

    private int page = 0;

    private int size = 10;

    /**
     * 默认构造函数.
     */
    public DefaultPageRequest() {
    }

    /**
     * 默认构造函数.
     * 
     * @param page 当前页位置(从0计), 默认为0
     * @param size 当前页内容(记录)数, 默认为10
     */
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
