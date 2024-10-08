package com.softeer.podo.log.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.softeer.podo.common.utils.MdcUtils.*;

/**
 * 일반적인 Json Body를 로깅하기 위해서 Mdc에 저장하는 필터
 */
public class RequestJsonBodyCollectionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        RequestWrapper wrapRequest = RequestWrapper.of(request);
        setJsonValueAndPutMdc(HEADER_MAP_MDC, wrapRequest.headerMap());
        setJsonValueAndPutMdc(PARAMETER_MAP_MDC, wrapRequest.parameterMap());
        putMdc(BODY_MDC, wrapRequest.getContents());
        putMdc(REQUEST_URI_MDC, wrapRequest.getRequestUri());

        try {
            filterChain.doFilter(wrapRequest, response);
        } finally {
            clear();
        }

    }
}
