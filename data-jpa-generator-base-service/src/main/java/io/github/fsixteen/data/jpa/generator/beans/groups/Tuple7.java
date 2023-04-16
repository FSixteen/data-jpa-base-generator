package io.github.fsixteen.data.jpa.generator.beans.groups;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 七元元组分组.<br>
 * 包含固定分组统计值.<br>
 * 
 * @author FSixteen
 * @since V1.0.0
 */
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class Tuple7 extends Tuple6 {

    @JsonInclude(value = Include.NON_NULL)
    private Object key7;

    public Tuple7(Object key, Object key2, Object key3, Object key4, Object key5, Object key6, Object key7, Object value) {
        super(key, key2, key3, key4, key5, key6, value);
        this.key7 = key7;
    }

    public Object getKey7() {
        return key7;
    }

    public void setKey7(Object key7) {
        this.key7 = key7;
    }

    @Override
    public Object[] toArray() {
        return new Object[] { this.getKey(), this.getKey2(), this.getKey3(), this.getKey4(), this.getKey5(), this.getKey6(), this.getKey7(), this.getValue() };
    }

}
