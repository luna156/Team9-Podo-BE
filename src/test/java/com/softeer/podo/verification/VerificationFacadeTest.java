package com.softeer.podo.verification;

import com.softeer.podo.admin.model.entity.Role;
import com.softeer.podo.common.VerificationFacadeBase;
import com.softeer.podo.common.utils.NumberUtils;
import com.softeer.podo.security.jwt.TokenInfo;
import com.softeer.podo.verification.exception.TokenNotMatchException;
import com.softeer.podo.verification.model.dto.CheckVerificationRequestDto;
import com.softeer.podo.verification.model.dto.CheckVerificationResponseDto;
import com.softeer.podo.verification.model.dto.ClaimVerificationRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VerificationFacadeTest extends VerificationFacadeBase {

    @Test
    @DisplayName("전화번호로 인증번호 요청하기 (성공)")
    public void validationCreateToken_Success() {
        // given
        doReturn(
                NumberUtils.generateRandom8DigitNumber()
        ).when(verificationService).createAndSaveCode(TEST_NAME, TEST_PHONENUM);

        // when
        verificationFacade.claimVerificationCode(
                new ClaimVerificationRequestDto(TEST_NAME, TEST_PHONENUM)
        );
    }

    @Test
    @DisplayName("사용자 정보와 인증번호가 일치하는지 확인 (성공)")
    public void checkVerificationCodeValid_Success() {
        // given
        doReturn(true).when(verificationService)
                .isVerificationCodeValid(TEST_NAME, TEST_PHONENUM, CODE_SAMPLE);
        doReturn(new TokenInfo(TOKEN_SAMPLE, 3600000L)).when(tokenProvider)
                .createAccessToken(TEST_NAME, TEST_PHONENUM, Role.ROLE_USER);

        // when
        CheckVerificationResponseDto result = verificationFacade.checkVerification(
                new CheckVerificationRequestDto(TEST_NAME, TEST_PHONENUM, CODE_SAMPLE)
        );

        // then
        assertThat(result.getAccessToken()).isNotNull();
        assertThat(result.getExpireTime()).isNotNull();
    }

    @Test
    @DisplayName("사용자 정보와 인증번호가 일치하는지 확인 (실패 - Redis에서 인증번호를 찾을 수 없음)")
    public void checkVerificationCodeValid_FailOnRedis() {
        // given
        doReturn(false).when(verificationService)
                .isVerificationCodeValid(TEST_NAME, TEST_PHONENUM, CODE_SAMPLE);

        // when
        assertThatThrownBy(() -> verificationFacade.checkVerification(new CheckVerificationRequestDto(TEST_NAME, TEST_PHONENUM, CODE_SAMPLE)))
                .isInstanceOf(TokenNotMatchException.class)
                .hasMessage("전송한 토큰이 일치하지 않습니다.");
    }
}
