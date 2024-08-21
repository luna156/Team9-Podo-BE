package com.softeer.podo.common.utils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.*;

public class RequestUtils {

    public static Map<String, String> headerMap(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            // 클라이언트 식별정보는 저장하지 않음
            if (name.equals("user-agent")) {
                continue;
            }
            String value = request.getHeader(name);
            headerMap.put(name, value);
        }
        return headerMap;
    }

    public static String getRequestUri(HttpServletRequest request) {
        return "[" + request.getMethod() + "] " + request.getRequestURI();
    }

    public static String getParts(HttpServletRequest request) throws ServletException, IOException {
        // 멀티파트 데이터 처리
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        Collection<Part> parts = request.getParts();
        for (Part part : parts) {
            // 멀티파트 파일 처리 로직
            String name = part.getName();
//            String filename = part.get();
            // 파일 처리 로직 또는 로깅
//            sb.append("\"").append(name).append("\" : \"").append(filename).append("\",");
        }
        sb.append("}");
        return sb.toString();
    }

    public static Map<String, String> parameterMap(HttpServletRequest request) {
        Map<String, String> parameterMap = new HashMap<>();
        Map<String, String[]> parameterNames = request.getParameterMap();
        // 쿼리 파라미터의 이름(key)
        for (String key : parameterNames.keySet()) {
            // 쿼리 파라미터 값(value)
            String[] values = parameterNames.get(key);
            // ,로 구분하여 map에 저장
            StringJoiner valueString = new StringJoiner(",");
            if (values != null) {
                for (String value : values) {
                    valueString.add(value);
                }
            }
            parameterMap.put(key, valueString.toString());
        }
        return parameterMap;
    }
}
