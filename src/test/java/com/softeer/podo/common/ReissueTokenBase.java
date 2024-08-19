package com.softeer.podo.common;

import com.softeer.podo.security.jwt.TokenProvider;
import com.softeer.podo.verification.facade.VerificationFacade;
import com.softeer.podo.verification.service.MessageService;
import com.softeer.podo.verification.service.VerificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReissueTokenBase {

    @InjectMocks
    protected VerificationService verificationService;

    @Mock
    protected TokenProvider tokenProvider;

    protected String TEST_NAME;
    protected String TEST_PHONENUM;
    protected String CODE_SAMPLE;
    protected String TOKEN_SAMPLE;
    protected String NEW_TOKEN_SAMPLE;

    @BeforeEach
    public void setUp() {
        TEST_NAME = "testName";
        TEST_PHONENUM = "01012345678";
        CODE_SAMPLE = "12345678";
        TOKEN_SAMPLE = "Bearer eyJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..hRDmq0EgKFN5Xhhs.e4vP5Vh517yFxXa8ZklfjqprAXMFy8ggA3KSEOhd4pQGNWcM-eBaYblWJqROgyj64vyahPunz8G--JDXXKD5nhSau_5CwX9Ie3qGyJ5gg3Sew2fs6Gg56BJkehUGzEnf7WT5w8zrnrKNkmhpH2yqDrHbIq5QbFnk3R6yHYND2qa0rck9WG7KSUghgAZ5W8mRHrYZKTelF58IDId5CGBf6a9RvJko0h-LO_4jpg5vJI3ajPPDXJLcp5OJbfFvCxQ.iMAmO-uVvKSObi-9W-_BAw";
        NEW_TOKEN_SAMPLE = "Bearer eyJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..x1a6G5Ykkk7qcePr.nMSPGoUTLE_L90CkIrFmZ--v2kdcoMxcxa61IVnsF5wVToMBA0-U5nI7hj8RFLSSKM1-V15ZzVO9MD2FgsH0lCR7gEgVklt7hehFrhNdN8IjpkFoU7MRMIAUhc78FyRovVBcIQbaPwMa7boTZC1scCyeY9Yi_vNRnbm75gymGlRvczsO4hEkHowHX_Mkbpe0muEv7xwnpzvg0Thh9nOfCX1USLXUfVSPI5_QHVBlwJzjYOdPs_ebHTgGGqmJ104.cnjO6FpYUWf50JdnHIdOyx";
    }

    @AfterEach
    public void tearDown() {
    }
}
