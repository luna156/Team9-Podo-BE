package com.softeer.podo.security;

import com.nimbusds.jwt.JWTClaimsSet;
import com.softeer.podo.admin.model.entity.Role;
import com.softeer.podo.common.TokenProviderBase;
import com.softeer.podo.security.jwt.TokenInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenProviderTest extends TokenProviderBase {

    @Test
    @DisplayName("JWT 토큰 검증 (성공)")
    public void validationCreateToken_Success() {
        // when
        JWTClaimsSet claimsSet = tokenProvider.validateTokenAndGetClaimsSet(tokenProvider.resolveToken(tokenSample.getToken()));

        // then
        assertThat(claimsSet.getClaim("name"))
                .isEqualTo("testname");
        assertThat(claimsSet.getClaim("number"))
                .isEqualTo("01012345678");
        assertThat(claimsSet.getClaim("ROLE_"))
                .isEqualTo(String.valueOf(Role.ROLE_USER));
    }

    @Test
    @DisplayName("헤더에서 Jwt 토큰 추출 (성공)")
    public void resolveToken_Success() {
        // when
        String resolvedToken = tokenProvider.resolveToken(headerToken);

        // then
        assertThat(resolvedToken).isEqualTo(headerToken.replace("Bearer ", ""));
    }

    @Test
    @DisplayName("헤더에서 Jwt 토큰 추출 (실패 - Bearer 토큰이 아닌 경우)")
    public void resolveToken_FailOnInvalidToken() {
        // when
        String resolvedToken = tokenProvider.resolveToken(headerToken.replace("Bearer ", ""));

        // then
        assertThat(resolvedToken).isNull();
    }

    @Test
    @DisplayName("암호화된 Jwt 토큰 만들기 - 성공")
    public void generateEncryptedToken_Success() {
        // when
        TokenInfo result = tokenProvider.createAccessToken(
                TEST_NAME,
                TEST_PHONENUM,
                Role.ROLE_USER
                );

        // then
        assertThat(result.getToken()).isNotNull();
        assertThat(result.getExpireTime()).isEqualTo(1000*60*60);
    }
}
