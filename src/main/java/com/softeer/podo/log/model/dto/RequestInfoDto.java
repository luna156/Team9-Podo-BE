package com.softeer.podo.log.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestInfoDto {
    private String requestPath;
    private String requestHeader;
    private String requestBody;
}
