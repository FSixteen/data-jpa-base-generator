package io.github.fsixteen.data.jpa.generator.beans.groups;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 单元元组分组.<br>
 * 包含固定分组统计值.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class Tuple {

    @JsonInclude(value = Include.NON_NULL)
    private Object key;

    @JsonInclude(value = Include.NON_NULL)
    private Object value;

    /**
     * 构造函数.<br>
     * 
     * @param key   Key
     * @param value Value
     */
    public Tuple(Object key, Object value) {
        super();
        this.key = key;
        this.value = value;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object[] toArray() {
        return new Object[] { this.getKey(), this.getValue() };
    }

}
