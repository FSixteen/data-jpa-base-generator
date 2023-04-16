package io.github.fsixteen.data.jpa.generator.base.entities;

/**
 * 主键字段-持久层端.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
public class StringIdEntity implements IdEntity<String> {

    private static final long serialVersionUID = 1L;

    public String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
