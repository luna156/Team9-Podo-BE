package com.softeer.podo.verification.service;

import com.softeer.podo.common.utils.NumberUtils;
import com.softeer.podo.event.model.entity.Role;
import com.softeer.podo.security.jwt.TokenInfo;
import com.softeer.podo.security.jwt.TokenProvider;
import com.softeer.podo.verification.model.dto.response.ReissueTokenResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class VerificationService {

    private final RedisService redisService;
    private final TokenProvider tokenProvider;
    private final Duration EXPIRE_TIME = Duration.ofMinutes(3);

    /**
     * 랜덤 키를 생성해서 키-인증번호 쌍을 Redis에 저장한다. (인증번호 만료시간: 3min)
     * key = (이름+전화번호)
     * value = 랜덤으로 생성된 8자리 인증번호
     * @return 생성된 인증번호
     */
    @Transactional
    public String createAndSaveCode(String name, String phoneNum) {
        String key = name+phoneNum;
        String value = NumberUtils.generateRandom8DigitNumber();
        redisService.setValues(key, value, EXPIRE_TIME);
        return value;
    }

    /**
     * 인증번호가 redis에 저장된 이름+번호를 key로 하는 value값(인증번호)와 일치하는지 여부를 체크한다.
     * @param name 인증 요청 유저명
     * @param phoneNum 인증 요청 유저 번호
     * @param verificationCode 인증번호
     * @return 인증번호 일치 여부
     */
    @Transactional
    public boolean isVerificationCodeValid(
            String name,
            String phoneNum,
            String verificationCode
    ) {
        //임시 만능 키
        if(verificationCode.equals("654321")) return true;
        String key = name+phoneNum;
        String value = redisService.getValues(key);
        return value.equals(verificationCode);
    }

    /**
     * 인증된 토큰으로 요청할 경우, 새로운 토큰을 발급해준다.
     * @param name 사용자 이름
     * @param phoneNum 사용자 전화번호
     * @return 새로운 accessToken
     */
    @Transactional
    public ReissueTokenResponseDto reissueToken(
            String name,
            String phoneNum
    ) {
        TokenInfo newToken = tokenProvider.createAccessToken(name, phoneNum, Role.ROLE_USER);
        return new ReissueTokenResponseDto(newToken.getToken(), newToken.getExpireTime());
    }
}
