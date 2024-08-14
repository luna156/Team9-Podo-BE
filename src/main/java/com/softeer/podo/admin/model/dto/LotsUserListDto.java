package com.softeer.podo.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LotsUserListDto implements UserListDto {
	int totalPage;
	int currentPage;
	List<LotsUserDto> lotsUserList;
}
