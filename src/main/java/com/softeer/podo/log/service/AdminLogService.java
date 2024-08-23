package com.softeer.podo.log.service;

import com.softeer.podo.admin.model.dto.response.GetAdminLogListResponseDto;
import com.softeer.podo.log.mapper.AdminLogMapper;
import com.softeer.podo.log.model.dto.RequestInfoDto;
import com.softeer.podo.log.model.entity.AdminLog;
import com.softeer.podo.log.repository.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminLogService {

    private final AdminLogRepository adminLogRepository;

    private final int PAGE_SIZE = 10;

    /**
     * Admin의 특정 동작을 rdb에 저장
     * @param dto 요청 uri, 헤더, body 정보
     */
    @Transactional
    public void createAdminLog(
            RequestInfoDto dto
    ) {
        adminLogRepository.save(
                AdminLog.builder()
                        .requestPath(dto.getRequestPath())
                        .requestHeader(dto.getRequestHeader())
                        .requestBody(dto.getRequestBody())
                        .build()
        );
    }

    /**
     * Admin 로그 전체 불러오기
     * @param pageNo 페이징 번호
     * @return 어드민 로그 목록
     */
    @Transactional(readOnly = true)
    public GetAdminLogListResponseDto getAdminLogs(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));
        Page<AdminLog> logs = adminLogRepository.findAll(pageable);
        return AdminLogMapper.AdminLogPageToAdminLogDtoList(logs);
    }
}
