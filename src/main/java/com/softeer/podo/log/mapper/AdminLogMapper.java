package com.softeer.podo.log.mapper;


import com.softeer.podo.admin.model.dto.AdminLogDto;
import com.softeer.podo.admin.model.dto.response.GetAdminLogListResponseDto;
import com.softeer.podo.log.model.entity.AdminLog;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class AdminLogMapper {

	public static GetAdminLogListResponseDto AdminLogPageToAdminLogDtoList(Page<AdminLog> adminLogPage) {
		List<AdminLogDto> adminLogDtoList = new ArrayList<>();
		for(AdminLog log : adminLogPage.getContent()) {
			//reward를 제외한 내용 추가
			adminLogDtoList.add(
					AdminLogDto.builder()
							.id(log.getId())
                            .requestPath(log.getRequestPath())
                            .requestHeader(log.getRequestHeader())
                            .requestBody(log.getRequestBody())
							.build()
			);
		}
		return new GetAdminLogListResponseDto(adminLogPage.getTotalPages(), adminLogPage.getNumber(), adminLogDtoList);
	}

}
