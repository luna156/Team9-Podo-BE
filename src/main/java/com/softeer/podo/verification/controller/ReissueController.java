package com.softeer.podo.verification.controller;

import com.softeer.podo.common.response.CommonResponse;
import com.softeer.podo.security.Auth;
import com.softeer.podo.security.AuthInfo;
import com.softeer.podo.verification.model.dto.response.ReissueTokenResponseDto;
import com.softeer.podo.verification.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
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
    public CommonResponse<ReissueTokenResponseDto> reissueToken(@Auth AuthInfo authInfo) {
        return new CommonResponse<>(verificationService.reissueToken(authInfo.getName(), authInfo.getPhoneNum()));
    }
}
