package com.softeer.podo.admin.model.dto.response;

import com.softeer.podo.admin.model.dto.AdminLogDto;
import lombok.*;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetAdminLogListResponseDto {
    int totalPage;
    int currentPage;
	private List<AdminLogDto> adminLogList;
}
