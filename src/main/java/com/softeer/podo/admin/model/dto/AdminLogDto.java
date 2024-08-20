package com.softeer.podo.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminLogDto {
    private Long id;
    private String requestPath;
    private String requestHeader;
    private String requestBody;
}
