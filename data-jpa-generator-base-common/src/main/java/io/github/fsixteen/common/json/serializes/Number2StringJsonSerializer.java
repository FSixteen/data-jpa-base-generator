package io.github.fsixteen.common.json.serializes;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * {@link Number} 类型数据转 {@link String} 类型数据 {@link JsonSerializer} 序列化模版.<br>
 *
 * 
 * @author FSixteen
 * @since 1.0.1
 */
public class Number2StringJsonSerializer extends JsonSerializer<Number> {

    @Override
    public void serialize(Number value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.nonNull(value)) {
            gen.writeString(Objects.toString(value));
        } else {
            gen.writeNull();
        }
    }

}