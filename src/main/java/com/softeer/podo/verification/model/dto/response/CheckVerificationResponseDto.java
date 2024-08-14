package com.softeer.podo.verification.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckVerificationResponseDto {
    private String accessToken;
    private Long expireTime;
}
