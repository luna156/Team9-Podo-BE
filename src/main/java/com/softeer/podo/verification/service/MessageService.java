package com.softeer.podo.verification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageReleaseService messageReleaseService;

    /**
     * 메시지 서비스의 경우 외부 API와 연결되어 I/O cost가 크므로 비동기로 요청한다.
     * @param phoneNum 인증코드를 발송할 휴대폰 전화번호
     * @param code 발송할 인증코드
     */
    public CompletableFuture<Void> sendVerificationMessage(String phoneNum, String code) {
        return messageReleaseService.sendVerificationMessage(phoneNum, code);
    }

}
