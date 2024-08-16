package com.softeer.podo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softeer.podo.log.filter.RequestCollectionFilter;
import com.softeer.podo.security.jwt.ExceptionHandleFilter;
import com.softeer.podo.security.jwt.JwtAuthenticationFilter;
import com.softeer.podo.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthenticationFilter(tokenProvider));
        registrationBean.addUrlPatterns("/test/auth"); // 필터를 적용할 URL 패턴
        registrationBean.addUrlPatterns("/v1/*"); // 필터를 적용할 URL 패턴
        registrationBean.setOrder(2); // 필터의 순서 (숫자가 낮을수록 먼저 실행됨)
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<ExceptionHandleFilter> exceptionHandleFilter() {
        FilterRegistrationBean<ExceptionHandleFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ExceptionHandleFilter(objectMapper));
        registrationBean.addUrlPatterns("/test/auth"); // 필터를 적용할 URL 패턴
        registrationBean.addUrlPatterns("/admin/*"); // 필터를 적용할 URL 패턴
        registrationBean.addUrlPatterns("/v1/*"); // 필터를 적용할 URL 패턴
        registrationBean.setOrder(1); // 필터의 순서 (숫자가 낮을수록 먼저 실행됨)
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RequestCollectionFilter> requestCollectionFilter() {
        FilterRegistrationBean<RequestCollectionFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestCollectionFilter());
        registrationBean.addUrlPatterns("/admin/*"); // 필터를 적용할 URL 패턴
        registrationBean.setOrder(3); // 필터의 순서 (숫자가 낮을수록 먼저 실행됨)
        return registrationBean;
    }
}