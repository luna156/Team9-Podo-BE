package com.softeer.podo.log.aop;

import com.softeer.podo.admin.model.dto.EventDto;
import com.softeer.podo.admin.model.dto.response.ConfigEventRewardResponseDto;
import com.softeer.podo.common.response.CommonResponse;
import com.softeer.podo.common.utils.MdcUtils;
import com.softeer.podo.log.model.dto.RequestInfoDto;
import com.softeer.podo.log.service.AdminLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AdminLogAspect {

    private final AdminLogService adminLogService;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void deleteMapping() {
    }

    // 선착순 이벤트 수정 로깅
    @AfterReturning(value = "execution(* com.softeer.podo.admin.controller..*Controller.configArrivalEvent(..)) && (putMapping())", returning = "result")
    public void arrivalLogging(JoinPoint joinPoint, CommonResponse<EventDto> result) {
        log.info("[Admin][ARRIVAL-CONFIG] event registered");
        RequestInfoDto requestInfo = getRequestInfo();
//        log.info("path = [{}]\n header = [{}]\n body = [{}]", requestInfo.getRequestPath(), requestInfo.getRequestHeader(), requestInfo.getRequestBody());
        adminLogService.createAdminLog(requestInfo);
    }

    // 랜덤추첨 이벤트 수정 로깅
    @AfterReturning(value = "execution(* com.softeer.podo.admin.controller..*Controller.configLotsEvent(..)) && (putMapping())", returning = "result")
    public void lotsLogging(JoinPoint joinPoint, CommonResponse<EventDto> result) {
        log.info("[Admin][LOTS-CONFIG] event entered");
        RequestInfoDto requestInfo = getRequestInfo();
        adminLogService.createAdminLog(requestInfo);
    }

    // 선착순 이벤트 상품 수정 로깅
    @AfterReturning(value = "execution(* com.softeer.podo.admin.controller..*Controller.configArrivalEventReward(..)) && (putMapping())", returning = "result")
    public void arrivalRewardLogging(JoinPoint joinPoint, CommonResponse<ConfigEventRewardResponseDto> result) {
        log.info("[Admin][ARRIVAL-REWARD-CONFIG] event entered");
        RequestInfoDto requestInfo = getRequestInfo();
        adminLogService.createAdminLog(requestInfo);
    }

    // 랜덤추첨 이벤트 상춤 수정 로깅
    @AfterReturning(value = "execution(* com.softeer.podo.admin.controller..*Controller.configLotsEventReward(..)) && (putMapping())", returning = "result")
    public void lotsRewardLogging(JoinPoint joinPoint, CommonResponse<ConfigEventRewardResponseDto> result) {
        log.info("[Admin][LOTS-REWARD-CONFIG] event entered");
        RequestInfoDto requestInfo = getRequestInfo();
        adminLogService.createAdminLog(requestInfo);
    }

    // 어드민 에러 로깅
    @AfterThrowing(value = "execution(* com.softeer.podo.admin.controller..*Controller.*(..))", throwing = "ex")
    public void eventErrorLogging(JoinPoint joinPoint, Exception ex) {
        log.info("[Admin][ERROR] error occurred - exception: {}, timestamp: {}", ex.getMessage(), System.currentTimeMillis());
    }

    private RequestInfoDto getRequestInfo() {
        String requestPath = MdcUtils.getFromMdc(MdcUtils.REQUEST_URI_MDC) + parsePath(MdcUtils.getFromMdc(MdcUtils.PARAMETER_MAP_MDC));
        String requestHeader = MdcUtils.getFromMdc(MdcUtils.HEADER_MAP_MDC);
        String requestBody = MdcUtils.getFromMdc(MdcUtils.BODY_MDC);

        return new RequestInfoDto(requestPath, requestHeader, requestBody);
    }

    private String parsePath(String parameterMap) {
        StringBuilder queryString = new StringBuilder();

        if(parameterMap.length()==2) {
            return "";
        }

        parameterMap.replaceAll("[\"}{]", "").split(",");

        for(String entry: parameterMap.split(",")) {
            int idx = entry.indexOf(":");
            queryString.append(entry, 0, idx)
                    .append("=")
                    .append(entry.substring(idx + 1))
                    .append("&");
        }

        return "?"+queryString.substring(0, queryString.length() - 1);
    }

}
