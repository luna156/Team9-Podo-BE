package com.softeer.podo.event.service;

import com.softeer.podo.event.exception.ExistingUserException;
import com.softeer.podo.event.model.dto.WordCloudResponseDto;
import com.softeer.podo.event.model.dto.request.LotsApplicationRequestDto;
import com.softeer.podo.event.model.dto.request.LotsCommentRequestDto;
import com.softeer.podo.event.model.dto.request.LotsTypeRequestDto;
import com.softeer.podo.event.model.dto.response.LotsApplicationResponseDto;
import com.softeer.podo.event.model.dto.response.LotsCommentResponseDto;
import com.softeer.podo.event.model.dto.response.LotsTypeResponseDto;
import com.softeer.podo.event.model.entity.*;
import com.softeer.podo.event.repository.*;
import com.softeer.podo.security.AuthInfo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class EventLotsServiceTest {
	@Autowired
	private EventLotsService eventLotsService;

	@MockBean
	LotsUserRepository lotsUserRepository;

	@MockBean
	LotsCommentRepository lotsCommentRepository;

	@MockBean
	TestResultRepository testResultRepository;

	@MockBean
	LotsShareLinkRepository lotsShareLinkRepository;

	@MockBean
	KeyWordRepository keyWordRepository;

	private static String name;
	private static String phoneNum;
	private static Long resultId;
	private static String comment;
	private static String url;
	private static LotsUser lotsUser;
	private static LotsShareLink lotsShareLink;
	private static TestResult testResult;
	private static List<KeyWord> keyWordList;

	@BeforeAll
	static void setUp() {
		name = "test_user";
		phoneNum = "123456789";
		resultId = 3L;
		comment = "test_comment";
		url = "result_url";
		lotsUser = LotsUser.builder()
				.id(1L)
				.role(Role.ROLE_USER)
				.name(name)
				.phoneNum(phoneNum)
				.testResult(TestResult.builder()
						            .url(url)
						            .build())
				.build();
		lotsShareLink = new LotsShareLink(1L, lotsUser, 0L, "testlink");
		testResult = TestResult
				.builder()
				.id(resultId)
				.description("test_description")
				.url("result url")
				.build();
		keyWordList = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			KeyWord keyWord = new KeyWord((long) i, "keyword" + i, i);
			keyWordList.add(keyWord);
		}
	}

	@Test
	@Transactional
	@DisplayName("유형테스트 결과 제출 테스트")
	void getProperDriverType() {
		//given
		Mockito.when(testResultRepository.findById(any())).thenReturn(Optional.ofNullable(testResult));
		Mockito.when(testResultRepository.findByResult(any())).thenReturn(testResult);
		LotsTypeRequestDto requestDto = new LotsTypeRequestDto();
		requestDto.setAnswer1("A");
		requestDto.setAnswer2("B");
		requestDto.setAnswer3("A");
		requestDto.setAnswer4("B");

		//when
		LotsTypeResponseDto responseDto = eventLotsService.getProperDriverType(requestDto);

		//then
		assertNotNull(responseDto.getScenarioList());
		assertNotNull(responseDto.getResultId());
	}

	@Test
	@Transactional
	@DisplayName("이벤트 응모 테스트")
	void applyEvent() {
		//given
		Mockito.when(lotsUserRepository.existsByPhoneNum(phoneNum)).thenReturn(false);
		Mockito.when(lotsUserRepository.save(any())).thenReturn(lotsUser);
		AuthInfo authInfo = new AuthInfo(name, phoneNum, Role.ROLE_USER);
		Mockito.when(testResultRepository.findById(resultId)).thenReturn(Optional.ofNullable(testResult));
		LotsApplicationRequestDto requestDto = new LotsApplicationRequestDto(resultId);

		//when
		LotsApplicationResponseDto responseDto = eventLotsService.applyEvent(authInfo, requestDto);

		//then
		assertNotNull(responseDto.getUniqueLink());
	}

	@Test
	@Transactional
	@DisplayName("이벤트 중복 응모 테스트")
	void applyEventException() {
		//given
		Mockito.when(lotsUserRepository.existsByPhoneNum(phoneNum)).thenReturn(true);
		AuthInfo authInfo = new AuthInfo(name, phoneNum, Role.ROLE_USER);
		LotsApplicationRequestDto requestDto = new LotsApplicationRequestDto(resultId);

		//when & then
		assertThrows(ExistingUserException.class, () -> eventLotsService.applyEvent(authInfo, requestDto));
	}

	@Test
	@Transactional
	@DisplayName("comment 등록 테스트")
	void registerComment() {
		//given
		Mockito.when(lotsUserRepository.existsByPhoneNum(phoneNum)).thenReturn(true);
		Mockito.when(lotsUserRepository.findByPhoneNum(phoneNum)).thenReturn(Optional.of(lotsUser));
		AuthInfo authInfo = new AuthInfo(name, phoneNum, Role.ROLE_USER);
		LotsCommentRequestDto requestDto = new LotsCommentRequestDto(comment);

		//when
		LotsCommentResponseDto responseDto = eventLotsService.registerComment(authInfo, requestDto);

		//then
		assertEquals(responseDto.getComment().getComment(), comment);
	}

	@Test
	@Transactional
	@DisplayName("이벤트 목록 service")
	void getEventUrl() throws Exception {
		//given
		Mockito.when(lotsUserRepository.existsByPhoneNum(phoneNum)).thenReturn(false);
		Mockito.when(lotsUserRepository.save(any())).thenReturn(lotsUser);
		Mockito.when(lotsUserRepository.findById(1L)).thenReturn(Optional.ofNullable(lotsUser));
		Mockito.when(lotsShareLinkRepository.findByLotsUser(lotsUser)).thenReturn(Optional.of(lotsShareLink));
		Mockito.when(testResultRepository.findById(resultId)).thenReturn(Optional.ofNullable(testResult));
		AuthInfo authInfo = new AuthInfo(name, phoneNum, Role.ROLE_USER);
		LotsApplicationRequestDto requestDto = new LotsApplicationRequestDto(resultId);
		String link = URLDecoder.decode(eventLotsService.applyEvent(authInfo, requestDto).getUniqueLink(), StandardCharsets.UTF_8);
		String uniqueLink = link.substring(link.indexOf("/lots/link/") + "/lots/link/".length());

		//when
		String resultUrl = eventLotsService.getEventUrl(uniqueLink);

		//then
		assertEquals(url ,resultUrl);
	}

	@Test
	@Transactional
	@DisplayName("word cloud service 테스트")
	void getWordCloud(){
		//given
		Mockito.when(keyWordRepository.findAll()).thenReturn(keyWordList);

		//when
		WordCloudResponseDto wordCloudResponseDto = eventLotsService.getWordCloud();

		//then
		assertNotNull(wordCloudResponseDto.getWordList());
	}

}