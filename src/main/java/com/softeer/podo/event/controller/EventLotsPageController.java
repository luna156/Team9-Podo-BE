package com.softeer.podo.event.controller;

import com.softeer.podo.common.response.CommonResponse;
import com.softeer.podo.common.response.ErrorCode;
import com.softeer.podo.event.service.EventLotsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/lots")
@RestController
@RequiredArgsConstructor
public class EventLotsPageController {

    private final EventLotsService eventLotsService;

    /**
     * 고유 공유링크 클릭시 FE에서 호출하는 api
     * {UID}: 유저 id (암호화)
     */
    @GetMapping("/link/{uid}")
    @Operation(summary = "공유링크 클릭시 호출되는 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 응답"),
            @ApiResponse(responseCode = "400", description = "요청 형식 에러 - 형식이 잘못되었습니다."),
            @ApiResponse(responseCode = "400", description = "사용자가 존재하지 않습니다"),
            @ApiResponse(responseCode = "403", description = "토큰 형식이 잘못되었을때"),
            @ApiResponse(responseCode = "500", description = "공유 링크가 있었는데 접근이 안될때"),
            @ApiResponse(responseCode = "500", description = "공유링크 생성중 오류가 발생했습니다.")
    })
    public CommonResponse<String> shareLinkClick(@PathVariable String uid) {
        try {
            // 해당 유저에 해당하는 적절한 이벤트 결과 페이지 찾기
            String uniqueLink = eventLotsService.getEventUrl(uid);
            // 이벤트 결과 페이지 반환
            return new CommonResponse<>(uniqueLink);
        } catch (Exception e) {
            // aes 복호화 exception
            return new CommonResponse<>(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
