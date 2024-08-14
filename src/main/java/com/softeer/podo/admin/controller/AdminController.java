package com.softeer.podo.admin.controller;

import com.softeer.podo.admin.model.dto.*;
import com.softeer.podo.admin.model.dto.request.EventConfigRequestDto;
import com.softeer.podo.admin.model.dto.request.EventRewardConfigRequestDto;
import com.softeer.podo.admin.model.dto.response.EventListResponseDto;
import com.softeer.podo.admin.model.dto.response.EventRewardConfigResponseDto;
import com.softeer.podo.admin.model.dto.user.ArrivalUserListDto;
import com.softeer.podo.admin.model.dto.user.LotsUserListDto;
import com.softeer.podo.admin.model.entity.validation.LotsValidationSequence;
import com.softeer.podo.admin.service.AdminService;
import com.softeer.podo.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
	private final AdminService adminService;


	@GetMapping("/eventlist")
	@Operation(summary = "이벤트 목록 반환 Api")
	public CommonResponse<EventListResponseDto> eventList(){
		return new CommonResponse<>(adminService.getEventList());
	}

	@PutMapping("/arrival/config")
	@Operation(summary = "선착순 이벤트 수정 Api")
	public CommonResponse<EventDto> arrivalEventConfig(@RequestBody @Valid EventConfigRequestDto dto){
		return new CommonResponse<>(adminService.configArrivalEvent(dto));
	}

	@PutMapping("/lots/config")
	@Operation(summary = "랜덤추첨 이벤트 수정 Api")
	public CommonResponse<EventDto> lotsEventConfig(@RequestBody @Valid EventConfigRequestDto dto){
		return new CommonResponse<>(adminService.configLotsEvent(dto));
	}

	@PutMapping("/arrival/rewardconfig")
	@Operation(summary = "선착순 이벤트 상품 수정 Api")
	public CommonResponse<EventRewardConfigResponseDto> arrivalEventRewardConfig(@RequestBody @Valid EventRewardConfigRequestDto dto){
		return new CommonResponse<>(adminService.configArrivalEventReward(dto));
	}

	@PutMapping("/lots/rewardconfig")
	@Operation(summary = "랜덤추첨 이벤트 상품 수정 Api")
	public CommonResponse<EventRewardConfigResponseDto> lotsEventRewardConfig(@RequestBody @Validated(LotsValidationSequence.class) EventRewardConfigRequestDto dto){
		return new CommonResponse<>(adminService.configLotsEventReward(dto));
	}

	@GetMapping("/arrival/applicationList")
	@Operation(summary = "선착순 응모 인원 반환 Api")
	public CommonResponse<ArrivalUserListDto> arrivalApplicationList(@RequestParam(required = false, defaultValue = "0", value = "page") int pageNo){
		return new CommonResponse<>(adminService.getArrivalApplicationList(pageNo));
	}

	@GetMapping("/lots/applicationList")
	@Operation(summary = "랜덤추첨 응모 인원 반환 Api")
	public CommonResponse<LotsUserListDto> lotsApplicationList(@RequestParam(required = false, defaultValue = "0", value = "page") int pageNo){
		return new CommonResponse<>(adminService.getLotsApplicationList(pageNo));
	}

	@GetMapping("/lots/pickrandom")
	@Operation(summary = "랜덤추첨 Api")
	public CommonResponse<LotsUserListDto> pickRandomLotsUser(){
		return new CommonResponse<>(adminService.getLotsResult());
	}
}
