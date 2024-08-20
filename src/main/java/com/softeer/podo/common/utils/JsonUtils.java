package com.softeer.podo.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 자바 객체를 String(Json)으로 변환하는 메서드
     * @param object 변환할 자바 객체
     * @return 변환한 string
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
