package com.softeer.podo.verification.facade;

import com.softeer.podo.security.jwt.TokenInfo;
import com.softeer.podo.security.jwt.TokenProvider;
import com.softeer.podo.event.model.entity.Role;
import com.softeer.podo.verification.exception.TokenNotMatchException;
import com.softeer.podo.verification.model.dto.request.CheckVerificationRequestDto;
import com.softeer.podo.verification.model.dto.response.CheckVerificationResponseDto;
import com.softeer.podo.verification.model.dto.request.ClaimVerificationRequestDto;
import com.softeer.podo.verification.service.MessageService;
import com.softeer.podo.verification.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationFacade {

    private final MessageService messageService;
    private final VerificationService verificationService;
    private final TokenProvider tokenProvider;

    /**
     * 인증번호를 요청하는 퍼사드 메서드
     * 1. 랜덤한 인증번호를 생성하여 key(사용자 정보)-value(랜덤 인증번호) 형태로 Redis에 저장한 후, 만료시간(3min)을 설정한다.
     * 2. 생성한 인증번호를 사용자 정보(전화번호)로 보낸다.
     * @param dto 사용자 정보
     */
    @Transactional
    public void claimVerificationCode(ClaimVerificationRequestDto dto) {
        String createdCode = verificationService.createAndSaveCode(dto.getName(), dto.getPhoneNum());
//        messageService.sendVerificationMessage(dto.getPhoneNum(), createdCode);
        log.info("created code for {} = {}", dto.getName(), createdCode); // TODO("remove")
    }

    /**
     * 인증번호가 맞는지 확인하는 퍼사드 메서드
     * 1. 요청 body로 받은 인증번호가 Redis에 저장된 해당 사용자의 인증번호와 일치하는지 확인한다.
     * 2. Redis에 인증번호가 존재하지 않거나(만료), 인증번호가 일치하지 않은 경우(불일치) TokenNotMatchException 발생
     * @param dto 사용자 정보, 인증번호
     * @return 생성된 토큰 정보와 만료시간
     */
    @Transactional
    public CheckVerificationResponseDto checkVerification(CheckVerificationRequestDto dto) {
        if(verificationService.isVerificationCodeValid(dto.getName(), dto.getPhoneNum(), dto.getVerificationCode())) {
            TokenInfo token = tokenProvider.createAccessToken(dto.getName(), dto.getPhoneNum(), Role.ROLE_USER);

            return new CheckVerificationResponseDto(
                    token.getToken(), token.getExpireTime()
            );
        } else {
            throw new TokenNotMatchException("전송한 토큰이 일치하지 않습니다.");
        }
    }
}
