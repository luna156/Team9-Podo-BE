package com.softeer.podo.admin.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ArrivalUserListDto {
	int totalPage;
	int currentPage;
	List<ArrivalUserDto> arrivalUserList;
}
