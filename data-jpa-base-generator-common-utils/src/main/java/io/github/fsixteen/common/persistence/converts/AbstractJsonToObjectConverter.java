package io.github.fsixteen.common.persistence.converts;

import java.util.Objects;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 数据库数据类型 {@link String} 与实体数据类型 {@link T} 相互转换
 * {@link AttributeConverter} 接口.<br>
 * 
 * @param <T>
 * @author FSixteen
 * @since 1.0.2
 */
public abstract class AbstractJsonToObjectConverter<T> implements AttributeConverter<T, String> {

    private final ObjectMapper om;

    private final Class<T> targetClass;

    public AbstractJsonToObjectConverter(ObjectMapper om) {
        this(om, null);
    }

    public AbstractJsonToObjectConverter(Class<T> targetClass) {
        this(new ObjectMapper().registerModule(new SimpleModule()).registerModule(new JavaTimeModule()), targetClass);
    }

    public AbstractJsonToObjectConverter(ObjectMapper om, Class<T> targetClass) {
        this.om = om;
        this.targetClass = targetClass;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        try {
            if (Objects.isNull(attribute)) {
                return null;
            } else {
                return this.om.writeValueAsString(attribute);
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to convert object to JSON.", e);
        }
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        try {
            if (Objects.isNull(dbData)) {
                return null;
            } else {
                return (Objects.isNull(this.targetClass) ? this.om.readValue(dbData, this.targetClass) : this.om.readValue(dbData, new TypeReference<T>() {
                }));
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to convert JSON to object.", e);
        }
    }

}