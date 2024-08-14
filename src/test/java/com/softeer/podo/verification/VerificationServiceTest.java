package com.softeer.podo.verification;

import com.softeer.podo.common.VerificationServiceBase;
import com.softeer.podo.verification.exception.TokenNotMatchException;
import com.softeer.podo.verification.model.dto.CheckVerificationRequestDto;
import com.softeer.podo.verification.model.dto.CheckVerificationResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class VerificationServiceTest extends VerificationServiceBase {

    @Test
    @DisplayName("랜덤 인증번호 생성 테스트 (성공)")
    public void generateRandomVerificationKey_Success() {
        // when
        String createdCode = verificationService.createAndSaveCode(TEST_NAME, TEST_PHONENUM);

        // then
        assertThat(createdCode).isNotNull();
        assertThat(createdCode.length()).isEqualTo(8);
    }

    @Test
    @DisplayName("Redis와 인증번호 일치 여부 확인 테스트 (성공)")
    public void checkVerificationCodeValid() {
        // given
        doReturn("12345678").when(redisService)
                .getValues(TEST_NAME+TEST_PHONENUM);

        // when
        boolean result = verificationService.isVerificationCodeValid(
                TEST_NAME, TEST_PHONENUM, CODE_SAMPLE
        );

        // then
        assertThat(result).isTrue();
    }
}
