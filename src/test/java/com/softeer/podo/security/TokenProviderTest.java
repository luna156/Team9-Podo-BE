package com.softeer.podo.security;

import com.nimbusds.jwt.JWTClaimsSet;
import com.softeer.podo.admin.model.entity.Role;
import com.softeer.podo.security.jwt.TokenInfo;
import com.softeer.podo.security.jwt.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    private TokenInfo tokenSample;

    @BeforeEach
    public void setUp() {
        tokenSample = tokenProvider.createAccessToken(
                "testname",
                "01012345678",
                Role.ROLE_USER
        );
    }

    @Test
    @DisplayName("JWT 토큰 검증 (성공)")
    public void validationCreateToken() {
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
}
