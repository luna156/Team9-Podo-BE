package com.softeer.podo.log.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * InputStream으로 Body를 읽으려면 한번만 읽을 수 있기 때문에 컨트롤러에서 사용하기 위해서는 받아서 캐싱해주어야 한다
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private final HttpServletRequest request;
    private final ByteArrayOutputStream contents = new ByteArrayOutputStream();

    private RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.request = request;
        // request body를 ByteArrayOutputStream에 복사
        InputStream inputStream = super.getInputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            contents.write(buffer, 0, bytesRead);
        }
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    // contents를 복사하고 inputstream으로 내보내기 위한 객체 설정
    @Override
    public ServletInputStream getInputStream() throws IOException {
        IOUtils.copy(super.getInputStream(), contents); // request content를 복사

        return new ServletInputStream() {
            private final ByteArrayInputStream buffer = new ByteArrayInputStream(contents.toByteArray());

            @Override
            public boolean isFinished() {
                return buffer.available()==0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                throw new RuntimeException();
            }

            @Override
            public int read() {
                return buffer.read();
            }
        };
    }

    public String getContents() {
        return contents.toString(StandardCharsets.UTF_8);
    }

    /**
     * 헤더를 파싱하여 Map형태로 반환
     */
    public Map<String, String> headerMap() {
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

    /**
     * 쿼리 파라미터를 파싱하여 Map형태로 반환
     * <String, String[]> 형태의 Map에서 value를 파싱
     */
    public Map<String, String> parameterMap() {
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

    public String getRequestUri() {
        return "[" + request.getMethod() + "] " + request.getRequestURI();
    }

    public static RequestWrapper of(ServletRequest request) throws IOException {
        return of((HttpServletRequest) request);
    }

    private static RequestWrapper of(HttpServletRequest request) throws IOException {
        return new RequestWrapper(request);
    }
}
