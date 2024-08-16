package com.softeer.podo.log.service;

import com.softeer.podo.log.model.dto.RequestInfoDto;
import com.softeer.podo.log.model.entity.AdminLog;
import com.softeer.podo.log.repository.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminLogService {

    private final AdminLogRepository adminLogRepository;

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
}
