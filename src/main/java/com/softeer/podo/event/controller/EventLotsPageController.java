package com.softeer.podo.event.controller;

import com.softeer.podo.common.response.CommonResponse;
import com.softeer.podo.common.response.ErrorCode;
import com.softeer.podo.event.service.EventLotsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
