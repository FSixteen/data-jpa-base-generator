package io.github.fsixteen.data.jpa.generator.beans.groups;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author FSixteen
 * @since V1.0.0
 */
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class Tuple4 extends Tuple3 {

    @JsonInclude(value = Include.NON_NULL)
    private Object key4;

    public Tuple4(Object key, Object key2, Object key3, Object key4, Object value) {
        super(key, key2, key3, value);
        this.key4 = key4;
    }

    public Object getKey4() {
        return key4;
    }

    public void setKey4(Object key4) {
        this.key4 = key4;
    }

    @Override
    public Object[] toArray() {
        return new Object[] { this.getKey(), this.getKey2(), this.getKey3(), this.getKey4(), this.getValue() };
    }

}
