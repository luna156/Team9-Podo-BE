package com.softeer.podo.log.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.softeer.podo.common.utils.MdcUtils.*;

public class RequestCollectionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        RequestWrapper wrapRequest = RequestWrapper.of(request);
        setJsonValueAndPutMdc(HEADER_MAP_MDC, wrapRequest.headerMap());
        setJsonValueAndPutMdc(PARAMETER_MAP_MDC, wrapRequest.parameterMap());
        setJsonValueAndPutMdc(BODY_MDC, wrapRequest.getContents());
//        setJsonValueAndPutMdc(BODY_MDC, wrapRequest.body());
        putMdc(REQUEST_URI_MDC, wrapRequest.getRequestUri());

        try {
            filterChain.doFilter(wrapRequest, response);
        } finally {
            clear();
        }

    }
}
