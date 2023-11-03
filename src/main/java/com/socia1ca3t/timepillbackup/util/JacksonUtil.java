package com.socia1ca3t.timepillbackup.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JacksonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    // 将对象转成字符串
    public static String objectToString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // 将Map转成指定的Bean
    public static <T> T mapToBean(Map map, Class clazz) {
        try {
            return (T) mapper.readValue(objectToString(map), clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // 将Json字符串转成指定的Bean
    public static <T> T jsonToBean(String jsonStr, Class clazz) {
        try {
            return (T) mapper.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode strToJsonNode(String jsonString) {

        try {
            return mapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    // 将Json字符串转成指定的Bean
    public static <T> List<T> jsonToArrayList(String jsonStr, Class clazz) {

        CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
        try {
            return mapper.readValue(jsonStr, listType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // 将Bean转成Map
    public static Map beanToMap(Object obj) {
        try {
            return mapper.readValue(objectToString(obj), Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
