package com.softeer.podo.admin.controller;

import com.softeer.podo.admin.model.dto.ArrivalUserListDto;
import com.softeer.podo.admin.model.dto.EventDto;
import com.softeer.podo.admin.model.dto.LotsUserListDto;
import com.softeer.podo.admin.model.dto.request.ConfigEventRequestDto;
import com.softeer.podo.admin.model.dto.request.ConfigEventRewardRequestDto;
import com.softeer.podo.admin.model.dto.response.ConfigEventRewardResponseDto;
import com.softeer.podo.admin.model.dto.response.EventListResponseDto;
import com.softeer.podo.admin.model.dto.response.GetAdminLogListResponseDto;
import com.softeer.podo.admin.service.AdminService;
import com.softeer.podo.admin.validation.LotsValidationSequence;
import com.softeer.podo.common.response.CommonResponse;
import com.softeer.podo.log.service.AdminLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
	private final AdminService adminService;
    private final AdminLogService adminLogService;

	@GetMapping("/eventlist")
	@Operation(summary = "이벤트 목록 반환 Api")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "성공적인 응답"),
			@ApiResponse(responseCode = "400", description = "요청 형식 에러 - 형식이 잘못되었습니다."),
	})
	public CommonResponse<EventListResponseDto> getEventList(){
		return new CommonResponse<>(adminService.getEventList());
	}

	@PutMapping("/arrival/config")
	@Operation(summary = "선착순 이벤트 수정 Api")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "성공적인 응답"),
			@ApiResponse(responseCode = "500", description = "s3 이미지 저장 중 문제가 발생했습니다."),
	})
	public CommonResponse<EventDto> configArrivalEvent(
            @RequestPart(value = "dto") ConfigEventRequestDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ){
		return new CommonResponse<>(adminService.configArrivalEvent(dto, file));
	}

	@PutMapping("/lots/config")
	@Operation(summary = "랜덤추첨 이벤트 수정 Api")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "성공적인 응답"),
			@ApiResponse(responseCode = "500", description = "s3 이미지 저장 중 문제가 발생했습니다."),
	})
	public CommonResponse<EventDto> configLotsEvent(
            @RequestPart(value = "dto") @Valid ConfigEventRequestDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ){
		return new CommonResponse<>(adminService.configLotsEvent(dto, file));
	}

	@PutMapping("/arrival/rewardconfig")
	@Operation(summary = "선착순 이벤트 상품 수정 Api")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "성공적인 응답"),
			@ApiResponse(responseCode = "400", description = "요청 형식 에러 - 형식이 잘못되었습니다."),
			@ApiResponse(responseCode = "500", description = "이벤트를 찾을 수 없습니다."),
	})
	public CommonResponse<ConfigEventRewardResponseDto> configArrivalEventReward(@RequestBody @Valid ConfigEventRewardRequestDto dto){
		return new CommonResponse<>(adminService.configArrivalEventReward(dto));
	}

	@PutMapping("/lots/rewardconfig")
	@Operation(summary = "랜덤추첨 이벤트 상품 수정 Api")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "성공적인 응답"),
			@ApiResponse(responseCode = "400", description = "요청 형식 에러 - 형식이 잘못되었습니다."),
			@ApiResponse(responseCode = "500", description = "이벤트를 찾을 수 없습니다."),
	})
	public CommonResponse<ConfigEventRewardResponseDto> configLotsEventReward(@RequestBody @Validated(LotsValidationSequence.class) ConfigEventRewardRequestDto dto){
		return new CommonResponse<>(adminService.configLotsEventReward(dto));
	}

	@GetMapping("/arrival/applicationList")
	@Operation(summary = "선착순 응모 인원 반환 Api")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "성공적인 응답"),
			@ApiResponse(responseCode = "500", description = "이벤트를 찾을 수 없습니다."),
	})
	public CommonResponse<ArrivalUserListDto> getArrivalEventUserList(@RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
	                                                                  @RequestParam(required = false, value = "name") String name,
	                                                                  @RequestParam(required = false, value = "phoneNum") String phoneNum,
	                                                                  @RequestParam(required = false, value = "createdAt") String createdAt){
		return new CommonResponse<>(adminService.getArrivalApplicationList(pageNo, name, phoneNum, createdAt));
	}

	@GetMapping("/lots/applicationList")
	@Operation(summary = "랜덤추첨 응모 인원 반환 Api")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "성공적인 응답"),
			@ApiResponse(responseCode = "500", description = "이벤트를 찾을 수 없습니다."),
	})
	public CommonResponse<LotsUserListDto> getLotsEventUserList(@RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
	                                                            @RequestParam(required = false, value = "name") String name,
	                                                            @RequestParam(required = false, value = "phoneNum") String phoneNum,
	                                                            @RequestParam(required = false, value = "createdAt") String createdAt){
		return new CommonResponse<>(adminService.getLotsApplicationList(pageNo, name, phoneNum, createdAt));
	}

    @GetMapping("/log")
    @Operation(summary = "어드민 로그 리스트 조회")
    @ApiResponses(value = {
		    @ApiResponse(responseCode = "200", description = "성공적인 응답"),
    })
    public CommonResponse<GetAdminLogListResponseDto> getAdminLogs(@RequestParam(required = false, defaultValue = "0", value = "page") int pageNo) {
        return new CommonResponse<>(adminLogService.getAdminLogs(pageNo));
    }
}
