package com.softeer.podo.admin.controller;

import com.softeer.podo.admin.model.dto.*;
import com.softeer.podo.admin.model.dto.request.ConfigEventRequestDto;
import com.softeer.podo.admin.model.dto.request.ConfigEventRewardRequestDto;
import com.softeer.podo.admin.model.dto.response.EventListResponseDto;
import com.softeer.podo.admin.model.dto.response.ConfigEventRewardResponseDto;
import com.softeer.podo.admin.model.dto.ArrivalUserListDto;
import com.softeer.podo.admin.model.dto.LotsUserListDto;
import com.softeer.podo.admin.validation.LotsValidationSequence;
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
	public CommonResponse<EventListResponseDto> getEventList(){
		return new CommonResponse<>(adminService.getEventList());
	}

	@PutMapping("/arrival/config")
	@Operation(summary = "선착순 이벤트 수정 Api")
	public CommonResponse<EventDto> configArrivalEvent(@RequestBody @Valid ConfigEventRequestDto dto){
		return new CommonResponse<>(adminService.configArrivalEvent(dto));
	}

	@PutMapping("/lots/config")
	@Operation(summary = "랜덤추첨 이벤트 수정 Api")
	public CommonResponse<EventDto> configLotsEvent(@RequestBody @Valid ConfigEventRequestDto dto){
		return new CommonResponse<>(adminService.configLotsEvent(dto));
	}

	@PutMapping("/arrival/rewardconfig")
	@Operation(summary = "선착순 이벤트 상품 수정 Api")
	public CommonResponse<ConfigEventRewardResponseDto> configArrivalEventReward(@RequestBody @Valid ConfigEventRewardRequestDto dto){
		return new CommonResponse<>(adminService.configArrivalEventReward(dto));
	}

	@PutMapping("/lots/rewardconfig")
	@Operation(summary = "랜덤추첨 이벤트 상품 수정 Api")
	public CommonResponse<ConfigEventRewardResponseDto> configLotsEventReward(@RequestBody @Validated(LotsValidationSequence.class) ConfigEventRewardRequestDto dto){
		return new CommonResponse<>(adminService.configLotsEventReward(dto));
	}

	@GetMapping("/arrival/applicationList")
	@Operation(summary = "선착순 응모 인원 반환 Api")
	public CommonResponse<ArrivalUserListDto> getArrivalEventUserList(@RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
	                                                                  @RequestParam(required = false, value = "name") String name,
	                                                                  @RequestParam(required = false, value = "phoneNum") String phoneNum){
		return new CommonResponse<>(adminService.getArrivalApplicationList(pageNo, name, phoneNum));
	}

	@GetMapping("/lots/applicationList")
	@Operation(summary = "랜덤추첨 응모 인원 반환 Api")
	public CommonResponse<LotsUserListDto> getLotsEventUserList(@RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
	                                                            @RequestParam(required = false, value = "name") String name,
	                                                            @RequestParam(required = false, value = "phoneNum") String phoneNum){
		return new CommonResponse<>(adminService.getLotsApplicationList(pageNo, name, phoneNum));
	}

	@GetMapping("/lots/random")
	@Operation(summary = "랜덤추첨 Api")
	public CommonResponse<LotsUserListDto> pickRandomLotsUser(){
		return new CommonResponse<>(adminService.getLotsResult());
	}
}
