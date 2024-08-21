package com.softeer.podo.log.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;
import org.apache.catalina.core.ApplicationPart;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.softeer.podo.common.utils.MdcUtils.BODY_MDC;
import static com.softeer.podo.common.utils.MdcUtils.putMdc;

/**
 * InputStream으로 Body를 읽으려면 한번만 읽을 수 있기 때문에 컨트롤러에서 사용하기 위해서는 받아서 캐싱해주어야 한다
 * https://coderanch.com/t/781674/open-source/apache-common-FileUpload
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private final HttpServletRequest request;
    private final ByteArrayOutputStream contents = new ByteArrayOutputStream();
    private final String CRLF = "\r\n";
    static final String TMP_FILE_PATH = "./tmpFiles/";
    protected Collection<Part> parts;
    private String partJson;

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
    public Part getPart(String name) {
        // 그냥 getparts 호출해서 part 리스트 받아온 다음에 반환
        for (Part part : getParts()) {
            if (name.equals(part.getName())) {
                return part;
            }
        }
        return null;
    }

    @Override
    public Collection<Part> getParts() {
        // 앞단에서 null일경우 그냥 반환처리 해야될듯?
        if(parts != null)
            return parts;
        // contents에서 읽어서 byte 파싱해서 리스트 만들기
//        DiskFileItem diskFileItem = new DiskFileItem();
//        List<Map<String, String>> maps = new ArrayList<>();

        parts = new ArrayList<>();
        byte[] body = contents.toByteArray();
        byte[] delimiter = getBoundaryFromheader().getBytes(StandardCharsets.UTF_8);
        List<Info> infos = parseMultipart(body, delimiter);
        for (Info info : infos) {
            if(!info.getHeaders().get("Content-Disposition").contains("filename")) {
                partJson = new String(info.getBody());
                putMdc(BODY_MDC, partJson);
            }
        }

        // Info에서 정보를 추출하여 ApplicationPart객체 생성
//        for (Info info : infos) {
//            Map<String, String> headers = info.getHeaders();
//            Map<String, String> dispositionMap = parseContentDisposition(headers.get("Content-Disposition"));
//            String fieldName = dispositionMap.get("name"), contentType = headers.get("Content-Type"), fileName = dispositionMap.get("filename");
//            boolean isFormField;
//            int sizeThreshold = 0;
//            File repository = new File("./tmpFiles/"+fileName+"_tmp");
//            if(!headers.containsKey("filename")) { // 폼 데이터인 경우
//                isFormField = true;
//            } else { // 파일 데이터인 경우
//                isFormField = false;
//            }
//
////            FileItem fileItem = new DiskFileItem(fieldName, contentType, isFormField, fileName, sizeThreshold, repository);
//
//            File location = new File("./tmpFiles/"+fileName);
//        }

        File repository = new File(TMP_FILE_PATH);
        DiskFileItemFactory factory = new DiskFileItemFactory();
        FileUpload upload = new FileUpload();
        upload.setFileItemFactory(factory);
        upload.setFileSizeMax(31457280);
        upload.setSizeMax(31457280);
        List<FileItem> fileItems = null;
        try {
            fileItems = upload.parseRequest(new ServletRequestContext(this));
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        try {
            factory.setRepository(repository.getCanonicalFile());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        for (FileItem fileItem : fileItems) {
            ApplicationPart part = new ApplicationPart(fileItem, repository);
            parts.add(part);
        }

        return parts;
    }

    /**
     * multipart body를 boundary로 나누고, 각 multipart data를 Info에 담아 리스트로 반환
     */
    public List<Info> parseMultipart(byte[] body, byte[] boundary) {
        List<Info> infos = new ArrayList<>();
        int start = 0;

        while ((start = indexOf(body, boundary, start)) != -1) {
            int end = indexOf(body, boundary, start + boundary.length);
            if (end == -1) end = body.length;

            byte[] partBytes = Arrays.copyOfRange(body, start + boundary.length + CRLF.length(), end);
            Info info = parsePart(partBytes);
            if(info!=null)
                infos.add(info);
            else break;
            start = end;
        }

        return infos;
    }

    public Info parsePart(byte[] partBytes) {
        System.out.println("partBytes = " + new String(partBytes));
        int headersEnd = indexOf(partBytes, (CRLF + CRLF).getBytes(StandardCharsets.UTF_8), 0);
        if (headersEnd == -1) {
//            throw new RuntimeException("Invalid multipart format");
            return null;
        }

        String headersString = new String(partBytes, 0, headersEnd, StandardCharsets.UTF_8);
        byte[] body = Arrays.copyOfRange(partBytes, headersEnd + (CRLF + CRLF).length(), partBytes.length);

        Map<String, String> headers = new HashMap<>();
        String[] headerLines = headersString.split(CRLF);
        for (String header : headerLines) {
            int colonIndex = header.indexOf(":");
            if (colonIndex != -1) {
                headers.put(header.substring(0, colonIndex).trim(), header.substring(colonIndex + 1).trim());
            } else if (header.startsWith("Content-Disposition")) {
                String[] elements = header.split(";");
                for (String element : elements) {
                    String[] keyValue = element.trim().split("=");
                    if (keyValue.length == 2) {
                        headers.put(keyValue[0].trim(), keyValue[1].trim().replaceAll("\"", ""));
                    }
                }
            }
        }

        return new Info(headers, body);
    }

    /**
     * pattern이 data의 start index 이후에 존재하는지 확인하고, 확인한다면 시작 인덱스를 반환
     */
    private int indexOf(byte[] data, byte[] pattern, int start) {
        outer:
        for (int i=start; i<=data.length-pattern.length; i++) {
            for (int j=0; j<pattern.length; j++) {
                if (data[i+j] != pattern[j])
                    continue outer;
            }
            return i;
        }
        return -1;
    }

    /**
     * request에서 multipart boundary 정보를 찾는다.
     */
    private String getBoundaryFromheader() {
        String contentType = request.getHeader("Content-Type");
        String boundary = null;

        if (contentType != null && contentType.contains("multipart/form-data")) {
            int index = contentType.indexOf("boundary=");
            if (index != -1) {
                boundary = contentType.substring(index + "boundary=".length());
                // boundary 값에서 앞뒤에 여백이 있는지 확인하고 제거
                boundary = boundary.trim();
            }
        }

        return boundary;
    }

    public static Map<String, String> parseContentDisposition(String contentDisposition) {
        Map<String, String> parameters = new HashMap<>();

        // Remove the "Content-Disposition:" prefix if it exists
        String[] parts = contentDisposition.split(";", 2);
        if (parts.length < 2) return parameters;

        // Split the remaining part into individual parameters
        String[] parameterPairs = parts[1].split(";");
        for (String pair : parameterPairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                // Remove quotes around values
                String key = keyValue[0].trim();
                String value = keyValue[1].trim().replace("\"", "");
                parameters.put(key, value);
            }
        }

        return parameters;
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
