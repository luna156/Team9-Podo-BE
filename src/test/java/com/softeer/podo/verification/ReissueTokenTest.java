package com.softeer.podo.verification;

import com.softeer.podo.common.ReissueTokenBase;
import com.softeer.podo.common.VerificationFacadeBase;
import com.softeer.podo.common.utils.NumberUtils;
import com.softeer.podo.event.model.entity.Role;
import com.softeer.podo.security.jwt.TokenInfo;
import com.softeer.podo.verification.exception.TokenNotMatchException;
import com.softeer.podo.verification.model.dto.request.CheckVerificationRequestDto;
import com.softeer.podo.verification.model.dto.request.ClaimVerificationRequestDto;
import com.softeer.podo.verification.model.dto.response.CheckVerificationResponseDto;
import com.softeer.podo.verification.model.dto.response.ReissueTokenResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

public class ReissueTokenTest extends ReissueTokenBase {

    @Test
    @DisplayName("토큰 재발급 요청 (성공)")
    public void reissueToken_Success() {
        // given
        doReturn(
                new TokenInfo(NEW_TOKEN_SAMPLE, 360000L)
        ).when(tokenProvider).createAccessToken(TEST_NAME, TEST_PHONENUM, Role.ROLE_USER);

        // when
        ReissueTokenResponseDto result = verificationService.reissueToken(
                TEST_NAME, TEST_PHONENUM
        );

        // then
        assertThat(result.getAccessToken()).isEqualTo(NEW_TOKEN_SAMPLE);
        assertThat(result.getExpireTime()).isEqualTo(360000L);
    }
}
