package io.github.fsixteen.data.jpa.base.generator.beans.groups;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 双元元组分组.<br>
 * 包含固定分组统计值.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = { "hibernateLazyInitializer", "handler" })
public class Tuple2 extends Tuple {

    @JsonInclude(value = Include.NON_NULL)
    private Object key2;

    public Tuple2(Object key, Object key2, Object value) {
        super(key, value);
        this.key2 = key2;
    }

    public Object getKey2() {
        return key2;
    }

    public void setKey2(Object key2) {
        this.key2 = key2;
    }

    @Override
    public Object[] toArray() {
        return new Object[] { this.getKey(), this.getKey2(), this.getValue() };
    }

}
