package com.softeer.podo.event.controller;

import com.softeer.podo.common.response.CommonResponse;
import com.softeer.podo.event.model.dto.request.LotsApplicationRequestDto;
import com.softeer.podo.event.model.dto.response.LotsApplicationResponseDto;
import com.softeer.podo.event.model.dto.request.LotsCommentRequestDto;
import com.softeer.podo.event.model.dto.response.LotsCommentResponseDto;
import com.softeer.podo.event.service.EventLotsService;
import com.softeer.podo.security.Auth;
import com.softeer.podo.security.AuthInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/lots")
@RestController
@RequiredArgsConstructor
public class EventLotsController {

    private final EventLotsService eventLotsService;

    @PostMapping("/comment")
    @Operation(summary = "랜덤추천이벤트 기대평 등록용 Api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 응답"),
            @ApiResponse(responseCode = "400", description = "요청 형식 에러 - 형식이 잘못되었습니다."),
            @ApiResponse(responseCode = "400", description = "사용자가 존재하지 않습니다."),
            @ApiResponse(responseCode = "400", description = "이미 기대평을 작성했습니다."),
            @ApiResponse(responseCode = "400", description = "기대평이 200자 이상입니다.")
    })
    public CommonResponse<LotsCommentResponseDto> eventComment(
            @Auth AuthInfo authInfo,
            @Valid @RequestBody LotsCommentRequestDto dto
    ) {
        return new CommonResponse<>(eventLotsService.registerComment(authInfo, dto));
    }

    @PostMapping("/application")
    @Operation(summary = "랜덤추첨 이벤트 응모하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 응답"),
            @ApiResponse(responseCode = "400", description = "요청 형식 에러 - 형식이 잘못되었습니다."),
            @ApiResponse(responseCode = "400", description = "사용자가 존재하지 않습니다"),
            @ApiResponse(responseCode = "403", description = "토큰 형식이 잘못되었을때"),
            @ApiResponse(responseCode = "500", description = "공유 링크가 있었는데 접근이 안될때"),
            @ApiResponse(responseCode = "500", description = "공유링크 생성중 오류가 발생했습니다.")
    })
    public CommonResponse<LotsApplicationResponseDto> applyEvent(
            @Auth AuthInfo authInfo,
            @Valid @RequestBody LotsApplicationRequestDto dto
    ) {
        return new CommonResponse<>(eventLotsService.applyEvent(authInfo, dto));
    }

}
