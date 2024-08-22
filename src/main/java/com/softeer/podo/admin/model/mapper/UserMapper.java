package com.softeer.podo.admin.model.mapper;


import com.softeer.podo.admin.model.dto.ArrivalUserDto;
import com.softeer.podo.admin.model.dto.ArrivalUserListDto;
import com.softeer.podo.admin.model.dto.LotsUserDto;
import com.softeer.podo.admin.model.dto.LotsUserListDto;
import com.softeer.podo.event.model.entity.ArrivalUser;
import com.softeer.podo.event.model.entity.LotsUser;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

	public static ArrivalUserListDto ArrivalUserPageToArrivalUserListDto(Page<ArrivalUser> userPage) {
		List<ArrivalUserDto> arrivalUserDtoList = new ArrayList<>();
		for(ArrivalUser user : userPage.getContent()) {
			//reward를 제외한 내용 추가
			arrivalUserDtoList.add(
					ArrivalUserDto.builder()
							.id(user.getId())
							.name(user.getName())
							.phoneNum(user.getPhoneNum())
							.rank(user.getArrivalRank())
							.createdAt(user.getCreatedAt())
							.build()
			);
		}
		return new ArrivalUserListDto(userPage.getTotalPages(), userPage.getNumber(), arrivalUserDtoList);
	}

	public static LotsUserListDto LotsUserPageToLotsUserListDto(Page<LotsUser> userPage) {
		List<LotsUserDto> lotsUserDtoList = new ArrayList<>();
		for(LotsUser user : userPage.getContent()) {
			//reward를 제외한 내용 추가
			lotsUserDtoList.add(
					LotsUserDto.builder()
							.id(user.getId())
							.name(user.getName())
							.phoneNum(user.getPhoneNum())
							.createdAt(user.getCreatedAt())
							.reward(user.getReward())
							.comment((user.getLotsComment() == null) ? null : user.getLotsComment().getComment())
							.build()
			);
		}
		return new LotsUserListDto(userPage.getTotalPages(), userPage.getNumber(), lotsUserDtoList);
	}
}
