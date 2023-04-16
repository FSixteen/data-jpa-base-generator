package io.github.fsixteen.data.jpa.generator.beans.groups;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 五元元组分组.<br>
 * 包含固定分组统计值.<br>
 * 
 * @author FSixteen
 * @since V1.0.0
 */
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class Tuple5 extends Tuple4 {

    @JsonInclude(value = Include.NON_NULL)
    private Object key5;

    public Tuple5(Object key, Object key2, Object key3, Object key4, Object key5, Object value) {
        super(key, key2, key3, key4, value);
        this.key5 = key5;
    }

    public Object getKey5() {
        return key5;
    }

    public void setKey5(Object key5) {
        this.key5 = key5;
    }

    @Override
    public Object[] toArray() {
        return new Object[] { this.getKey(), this.getKey2(), this.getKey3(), this.getKey4(), this.getKey5(), this.getValue() };
    }

}
