package com.softeer.podo.verification.controller;

import com.softeer.podo.common.response.CommonResponse;
import com.softeer.podo.verification.exception.MessageSendFailException;
import com.softeer.podo.verification.facade.VerificationFacade;
import com.softeer.podo.verification.model.dto.request.CheckVerificationRequestDto;
import com.softeer.podo.verification.model.dto.response.CheckVerificationResponseDto;
import com.softeer.podo.verification.model.dto.request.ClaimVerificationRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationFacade verificationFacade;

    @PostMapping("/claim")
    @Operation(summary = "전화번호로 인증번호 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 응답"),
            @ApiResponse(responseCode = "403", description = "토큰 형식이 잘못되었을때"),
            @ApiResponse(responseCode = "500", description = "[비동기 에러] 메시지 발송에 실패했습니다")
    })
    public CompletableFuture<CommonResponse<Void>> claimVerificationCode(@Valid @RequestBody ClaimVerificationRequestDto dto) {
        return verificationFacade.claimVerificationCode(dto)
                .thenApply(result -> new CommonResponse<>(result))
                .exceptionally(ex -> {
                    throw new MessageSendFailException("[비동기 에러] 메시지 발송에 실패했습니다");
                });
    }

    @PostMapping("/check")
    @Operation(summary = "인증번호 검증")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 응답"),
            @ApiResponse(responseCode = "403", description = "토큰 형식이 잘못되었을때"),
            @ApiResponse(responseCode = "400", description = "인증 정보가 잘못되었거나 인증 시간이 초과되었습니다.")
    })
    public CommonResponse<CheckVerificationResponseDto> checkVerification(@Valid @RequestBody CheckVerificationRequestDto dto) {
        return new CommonResponse<>(verificationFacade.checkVerification(dto));
    }
}
