package com.softeer.podo.verification.controller;

import com.softeer.podo.common.response.CommonResponse;
import com.softeer.podo.security.Auth;
import com.softeer.podo.security.AuthInfo;
import com.softeer.podo.verification.model.dto.response.ReissueTokenResponseDto;
import com.softeer.podo.verification.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/reissue")
@RequiredArgsConstructor
public class ReissueController {

    private final VerificationService verificationService;

    @PostMapping
    @Operation(summary = "토큰 갱신")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 응답"),
            @ApiResponse(responseCode = "403", description = "토큰 형식이 잘못되었을때")
    })
    public CommonResponse<ReissueTokenResponseDto> reissueToken(@Auth AuthInfo authInfo) {
        return new CommonResponse<>(verificationService.reissueToken(authInfo.getName(), authInfo.getPhoneNum()));
    }
}
