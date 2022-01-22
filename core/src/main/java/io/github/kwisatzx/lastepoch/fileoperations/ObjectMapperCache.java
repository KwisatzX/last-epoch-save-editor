package io.github.kwisatzx.lastepoch.fileoperations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;

public class ObjectMapperCache {
    private final static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static <T> T readValue(String src, Class<T> clazz) {
        try {
            return objectMapper.readValue(src, clazz);
        } catch (JsonProcessingException e) {
            LoggerFactory.getLogger(ObjectMapperCache.class).error(e.getMessage());
        }
        return null;
    }

    public static String writeValueAsString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LoggerFactory.getLogger(ObjectMapperCache.class).error(e.getMessage());
        }
        return null;
    }
}
