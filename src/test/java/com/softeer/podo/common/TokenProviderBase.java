package com.softeer.podo.common;

import com.softeer.podo.event.model.entity.Role;
import com.softeer.podo.security.jwt.TokenInfo;
import com.softeer.podo.security.jwt.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TokenProviderBase {

    @Autowired
    protected TokenProvider tokenProvider;

    protected TokenInfo tokenSample;

    protected String headerToken;

    protected String TEST_NAME;
    protected String TEST_PHONENUM;
    protected String CODE_SAMPLE;
    protected Long ACCESS_TOKEN_VALID_TIME;

    @BeforeEach
    public void setUp() {
        TEST_NAME = "testName";
        TEST_PHONENUM = "01012345678";
        CODE_SAMPLE = "12345678";
        ACCESS_TOKEN_VALID_TIME = (long) (1000*60*60);

        tokenSample = tokenProvider.createAccessToken(
                "testname",
                "01012345678",
                Role.ROLE_USER
        );

        headerToken = "Bearer eyJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..hRDmq0EgKFN5Xhhs.e4vP4Vh517yFxXa7ZklfjqprAXMFy8ggA3KSEOhd4pQGNWcM-eBaYblWJqROgyj64vyahPunz8G--JDXXKD5nhSau_5CwX9Ie3qGyJ5gg3Sew2fs6Gg56BJkehUGzEnf7WT5w8zrnrKNkmhpH2yqDrHbIq5QbFnk3R6yHYND2qa0rck9WG7KSUghgAZ5W8mRHrYZKTelF58IDId5CGBf6a9RvJko0h-LO_4jpg5vJI3ajPPDXJLcp5OJbfFvCxQ.iMAmO-uVvKSObi-9W-_BAw";
    }
}
