package com.softeer.podo.event.controller;

import com.softeer.podo.common.response.CommonResponse;
import com.softeer.podo.event.model.dto.request.LotsTypeRequestDto;
import com.softeer.podo.event.model.dto.response.LotsTypeResponseDto;
import com.softeer.podo.event.model.dto.WordCloudResponseDto;
import com.softeer.podo.event.service.EventLotsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/lots")
@RestController
@RequiredArgsConstructor
public class EventLotsApiController {

    private final EventLotsService eventLotsService;

    /**
     * 제출한 유형테스트 결과에 따라 적절한 드라이버 유형 반환
     */
    @PostMapping("/type")
    @Operation(summary = "제출한 유형테스트 결과에 따라 적절한 드라이버 유형 반환")
    public CommonResponse<LotsTypeResponseDto> getDriverType(@Valid @RequestBody LotsTypeRequestDto dto) {
        return new CommonResponse<>(eventLotsService.getProperDriverType(dto));
    }

    /**
     *
     */
    @GetMapping("/wordCloud")
    @Operation(summary = "워드클라우드 단어 목록 및 빈도수 반환")
    public CommonResponse<WordCloudResponseDto> getWordCloud() {
        return new CommonResponse<>(eventLotsService.getWordCloud());
    }
}
