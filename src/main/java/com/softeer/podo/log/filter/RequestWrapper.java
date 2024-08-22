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
 * ServletRequestWrapper에서 사용될 ServletRequest의 default 구현체인 catalina.Request 메서드를 오버라이딩 해주기 위한 래퍼 클래스
 * 궁극적인 목표는 catalina.Request 대신 전달되어 json을 읽는(getInputStream) 메서드와 multipart/form-data를 읽는(getParts) 메서드의 동작을 오버라이딩 하는 것
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private final HttpServletRequest request;
    private final ByteArrayOutputStream contents = new ByteArrayOutputStream();
    private final String CRLF = "\r\n";
    private final Long DEFAULT_SIZE = 31457280L;
    private final String TMP_FILE_PATH = "./tmpFiles/";
    private final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    private final String USER_AGENT_HEADER = "user-agent";
    private final String CONTENT_TYPE_HEADER = "Content-Type";
    private final String MULTIPART_FORM_DATA = "multipart/form-data";

    protected Collection<Part> parts;

    /**
     * InputStream은 한번만 읽을 수 있기 때문에 컨트롤러에서 사용하기 위해서는 받아서 캐싱해주어야 한다.
     * 기존 HttpServletRequest(구현체: catalina.Request)의 InputStream을 읽고, ByteArrayOutputStream에 캐싱하여 반복 사용할 수 있도록 한다.
     * @param request 기존에 들어오는 Request
     */
    private RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.request = request;
        // request body를 ByteArrayOutputStream에 복사
        InputStream inputStream = super.getInputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while((bytesRead = inputStream.read(buffer))!=-1) {
            contents.write(buffer, 0, bytesRead);
        }
    }

    /**
     * getParts()를 호출하여 Collection<Part>를 호출한 다음, 하나씩 검사하며 이름이 동일한 Part를 반환한다.
     * @param name 찾으려는 Part 데이터의 이름
     * @return 찾은 Part 객체
     */
    @Override
    public Part getPart(String name) {
        // 그냥 getparts 호출해서 part 리스트 받아온 다음에 반환
        for(Part part: getParts()) {
            if(name.equals(part.getName())) {
                return part;
            }
        }
        return null;
    }

    /**
     * 들어오는 Part들의 정보를 읽어서 정보를 컬렉션으로 반환하는 메서드
     * DiskFileItemFactory를 사용하여 오버라이딩한 getInputStream()을 통해서 캐싱한 contents에서 multipart/form-data를 읽어서 파싱하게 함
     *
     * catalina.Request 내부를 따라가다 보면 (Request.parseParts()->FileUploadBase.parseRequest()->getItemIterator()->FileItemIteratorImpl)
     * ctx.getInputStream()과 같이 Request의 getInputStream()을 사용하고 있음을 알 수 있다.
     *
     * 따라서 FileUploadBase.parseRequest()도 Wrapper의 getInputStream()을 오버라이딩해주면 contents에 캐싱해놓은 body를 읽게 됨을 알 수 있다.
     *
     * @return 찾은 Part들을 Collection으로 반환
     */
    @Override
    public Collection<Part> getParts() {
        // 이미 파싱한 경우 이미 파싱된 객체를 재사용
        if(parts != null)
            return parts;

        // 로깅을 위해서 캐싱한 contents를 읽어서 Mdc에 저장
        parts = new ArrayList<>();
        byte[] body = contents.toByteArray();
        byte[] delimiter = getBoundaryFromHeader().getBytes(StandardCharsets.UTF_8);
        List<Info> infos = parseMultipart(body, delimiter);
        for(Info info: infos) {
            // 파일이 아니라면 로깅을 위해 저장
            if(!info.getHeaders().get(CONTENT_DISPOSITION_HEADER).contains("name=\"file\"")) {
                String partJson = new String(info.getBody());
                putMdc(BODY_MDC, partJson);
            }
        }

        File repository = new File(TMP_FILE_PATH);
        DiskFileItemFactory factory = new DiskFileItemFactory();
        FileUpload upload = new FileUpload();
        upload.setFileItemFactory(factory);
        upload.setFileSizeMax(DEFAULT_SIZE);
        upload.setSizeMax(DEFAULT_SIZE);
        List<FileItem> fileItems = null;
        try {
            fileItems = upload.parseRequest(new ServletRequestContext(this)); // Wrapper Request를 담는 컨텍스트를 생성해서 생성자 주입
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        try {
            factory.setRepository(repository.getCanonicalFile());
        } catch(IOException e) {
            e.printStackTrace();
        }
        for(FileItem fileItem : fileItems) {
            // 생성한 fileItem을 사용하여 Part의 구현체인 ApplicationPart타입 객체를 만들어 Part 컬렉션을 생성
            ApplicationPart part = new ApplicationPart(fileItem, repository);
            parts.add(part);
        }

        return parts;
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
            if (name.equals(USER_AGENT_HEADER)) {
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
        for(String key : parameterNames.keySet()) {
            // 쿼리 파라미터 값(value)
            String[] values = parameterNames.get(key);
            // ,로 구분하여 map에 저장
            StringJoiner valueString = new StringJoiner(",");
            if(values != null) {
                for(String value : values) {
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


    /**
     * multipart body를 boundary로 나누고, 각 multipart data를 Info에 담아 리스트로 반환
     * 데이터가 이미지 등으로 들어올 경우 String 변환시 손상될 수 있으므로 바이트 단위로 파싱하였다.
     */
    private List<Info> parseMultipart(byte[] body, byte[] boundary) {
        List<Info> infos = new ArrayList<>();
        int start = 0;

        while((start=indexOf(body, boundary, start))!=-1) {
            int end = indexOf(body, boundary, start + boundary.length);
            if (end==-1) end = body.length;

            byte[] partBytes = Arrays.copyOfRange(body, start + boundary.length + CRLF.length(), end);
            Info info = parsePart(partBytes);
            if(info!=null)
                infos.add(info);
            else break;
            start = end;
        }

        return infos;
    }

    private Info parsePart(byte[] partBytes) {
        int headersEnd = indexOf(partBytes, (CRLF+CRLF).getBytes(StandardCharsets.UTF_8), 0);
        // 더 이상 읽을 multipart data가 없는 경우 (delimiter 패턴이 없다면)
        if(headersEnd == -1) {
            return null;
        }

        String headersString = new String(partBytes, 0, headersEnd, StandardCharsets.UTF_8);
        byte[] body = Arrays.copyOfRange(partBytes, headersEnd+(CRLF+CRLF).length(), partBytes.length);

        Map<String, String> headers = new HashMap<>();
        String[] headerLines = headersString.split(CRLF);
        for(String header: headerLines) {
            int colonIndex = header.indexOf(":");
            if(colonIndex!=-1) {
                headers.put(header.substring(0, colonIndex).trim(), header.substring(colonIndex + 1).trim());
            } else if(header.startsWith(CONTENT_DISPOSITION_HEADER)) {
                String[] elements = header.split(";");
                for(String element: elements) {
                    String[] keyValue = element.trim().split("=");
                    if (keyValue.length==2) {
                        headers.put(keyValue[0].trim(), keyValue[1].trim().replaceAll("\"", ""));
                    }
                }
            }
        }

        return new Info(headers, body);
    }

    /**
     * pattern이 data의 start index 이후에 존재하는지 확인하고, 확인한다면 시작 인덱스를 반환
     * 패턴이 존재하지 않는다면 -1 반환
     */
    private int indexOf(byte[] data, byte[] pattern, int start) {
        outer:
        for (int i=start; i<=data.length-pattern.length; i++) {
            for (int j=0; j<pattern.length; j++) {
                if (data[i+j]!=pattern[j])
                    continue outer;
            }
            return i;
        }
        return -1;
    }

    /**
     * request에서 multipart/form-data의 part를 구분하는 boundary 문자열을 찾는다.
     */
    private String getBoundaryFromHeader() {
        String contentType = request.getHeader(CONTENT_TYPE_HEADER);
        String boundary = null;

        if (contentType!=null && contentType.contains(MULTIPART_FORM_DATA)) {
            int index = contentType.indexOf("boundary=");
            if (index!=-1) {
                boundary = contentType.substring(index + "boundary=".length());
                // boundary 값에서 앞뒤 여백 제거
                boundary = boundary.trim();
            }
        }
        return boundary;
    }

    public static RequestWrapper of(ServletRequest request) throws IOException {
        return of((HttpServletRequest) request);
    }

    private static RequestWrapper of(HttpServletRequest request) throws IOException {
        return new RequestWrapper(request);
    }
}
