package com.softeer.podo.common;

import com.softeer.podo.verification.service.RedisService;
import com.softeer.podo.verification.service.VerificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VerificationServiceBase {

    @InjectMocks
    protected VerificationService verificationService;

    @Mock
    protected RedisService redisService;

    protected String TEST_NAME;
    protected String TEST_PHONENUM;
    protected String CODE_SAMPLE;

    @BeforeEach
    public void setUp() {
        TEST_NAME = "testName";
        TEST_PHONENUM = "01012345678";
        CODE_SAMPLE = "12345678";

    }

    @AfterEach
    public void tearDown() {
    }
}
