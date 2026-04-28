package io.github.fsixteen.common.json.serializes;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * {@link byte[]} 类型数据转 {@link String} 类型数据 {@link JsonSerializer} 序列化模版.<br>
 * 
 * @author FSixteen
 * @since 1.0.3
 */
public class ByteArray2Base64JsonSerializer extends JsonSerializer<byte[]> {

    @Override
    public void serialize(byte[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.nonNull(value)) {
            gen.writeString(Base64.getEncoder().encodeToString(value));
        } else {
            gen.writeNull();
        }
    }

}