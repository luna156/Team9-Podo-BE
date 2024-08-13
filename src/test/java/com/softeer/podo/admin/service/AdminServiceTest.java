package com.softeer.podo.admin.service;

import com.softeer.podo.admin.model.dto.*;
import com.softeer.podo.admin.model.dto.user.ArrivalUserDto;
import com.softeer.podo.admin.model.dto.user.ArrivalUserListDto;
import com.softeer.podo.admin.model.dto.user.LotsUserDto;
import com.softeer.podo.admin.model.dto.user.LotsUserListDto;
import com.softeer.podo.admin.model.entity.*;
import com.softeer.podo.admin.repository.*;
import com.softeer.podo.event.model.entity.LotsComment;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AdminServiceTest {
	@MockBean
	private EventRepository eventRepository;

	@MockBean
	private ArrivalUserRepository arrivalUserRepository;

	@MockBean
	private LotsUserRepository lotsUserRepository;

	@MockBean
	private EventRewardRepository eventRewardRepository;

	@Autowired
	private AdminService adminService;

	private static Event arrivalEvent;
	private static Event lotsEvent;
	private static List<ArrivalUser> arrivalUserList;
	private static List<LotsUser> lotsUserList;

	@BeforeEach
	void init() {
		MockitoAnnotations.openMocks(this);
	}

	@BeforeAll
	static void setUp() {
		// Mock Arrival Event
		List<EventReward> arrivalEventRewards = new ArrayList<>();
		arrivalEventRewards.add(
				EventReward.builder()
						.numWinners(100)
						.rewardRank(1)
						.reward("스타벅스 커피 쿠폰")
						.event(arrivalEvent)
						.build()
		);
		arrivalEvent = Event.builder()
				.id(1L)
				.eventType(EventType.builder().type("arrival").build())
				.title("셀토스 선착순 이벤트")
				.description("The 2025 셀토스 출시 기념 선착순 이벤트")
				.repeatDay("1111100")
				.repeatTime(LocalTime.from(LocalDateTime.of(2024, 9, 6, 15, 0, 0)))
				.startAt(LocalDateTime.of(2024, 9, 6, 0, 0, 0))
				.endAt(LocalDateTime.of(2024, 9, 9, 18, 0, 0))
				.eventRewardList(arrivalEventRewards)
				.tagImage("image url")
				.build();

		// Mock Lots Event
		List<EventReward> lotsEventRewards = new ArrayList<>();
		lotsEventRewards.add(
				EventReward.builder()
						.numWinners(1)
						.rewardRank(1)
						.reward("시그니엘 숙박권")
						.event(lotsEvent)
						.build()
		);
		lotsEventRewards.add(
				EventReward.builder()
						.numWinners(3)
						.rewardRank(2)
						.reward("파인다이닝 식사권")
						.event(lotsEvent)
						.build()
		);
		lotsEventRewards.add(
				EventReward.builder()
						.numWinners(10)
						.rewardRank(3)
						.reward("현대백화점 상품권")
						.event(lotsEvent)
						.build()
		);
		EventWeight lotsEventWeight = EventWeight.builder()
				.times(3)
				.weightCondition("기대평")
				.event(lotsEvent)
				.build();
		lotsEvent = Event.builder()
				.id(2L)
				.eventType(EventType.builder().type("lots").build())
				.title("셀토스 추첨 이벤트")
				.description("The 2025 셀토스 출시 기념 추첨 이벤트")
				.startAt(LocalDateTime.of(2024, 9, 6, 0, 0, 0))
				.endAt(LocalDateTime.of(2024, 9, 9, 18, 0, 0))
				.tagImage("image url")
				.eventRewardList(lotsEventRewards)
				.eventWeight(lotsEventWeight)
				.build();

		// User List
		arrivalUserList = new ArrayList<>();
		lotsUserList = new ArrayList<>();
		for(int i = 1; i <= 200; i++){
			arrivalUserList.add(
					ArrivalUser.builder()
							.id((long) i)
							.name("test_user_"+ i)
							.arrivalRank(i)
							.phoneNum("test_phoneNum_" + i)
							.role(Role.ROLE_USER)
							.build()
			);
			LotsUser lotsUser = LotsUser.builder()
					.id((long) i)
					.name("test_user_"+ i)
					.phoneNum("test_phoneNum_" + i)
					.role(Role.ROLE_USER)
					.build();
			if(i % 3 == 0) lotsUser.setLotsComment(new LotsComment((long) (i / 3), lotsUser, "test_comment_" + i));
			lotsUserList.add(lotsUser);
		}
	}

	@Test
	@Transactional
	@DisplayName("이벤트 목록 service")
	void getEventList() {
		//given
		when(this.eventRepository.findAll()).thenReturn(Arrays.asList(arrivalEvent, lotsEvent));

		//when
		EventListResponseDto responseDto = adminService.getEventList();

		//then
		assertNotNull(responseDto);
		verify(eventRepository, times(1)).findAll();
	}

	@Test
	@Transactional
	@DisplayName("선착순 이벤트 수정 service")
	void configArrivalEvent() {
		//given
		when(eventRepository.findById(1L)).thenReturn(Optional.of(arrivalEvent));
		String title = "test";
		String description = "testDescription";
		String repeatDay = "1111100";
		LocalTime repeatTime = LocalTime.of(13, 00);
		LocalDateTime startAt = LocalDateTime.of(2024, 9, 6, 13, 00);
		LocalDateTime endAt = LocalDateTime.of(2024, 9, 6, 13, 00);
		String tagImage = "image url";
		EventConfigRequestDto requestDto = new EventConfigRequestDto(title, description, repeatDay, repeatTime, startAt, endAt, tagImage);

		//when
		EventDto responseDto = adminService.configArrivalEvent(requestDto);

		//then
		assertEquals(title, responseDto.getTitle());
		assertEquals(description, responseDto.getDescription());
		assertEquals(repeatDay, responseDto.getRepeatDay());
		assertEquals(repeatTime, responseDto.getRepeatTime());
		assertEquals(startAt, responseDto.getStartAt());
		assertEquals(endAt, responseDto.getEndAt());
		assertEquals(tagImage, responseDto.getTagImage());
	}

	@Test
	@Transactional
	@DisplayName("랜덤 추첨 이벤트 수정 service")
	void configLotsEvent() {
		//given
		when(eventRepository.findById(2L)).thenReturn(Optional.of(lotsEvent));
		String title = "test";
		String description = "testDescription";
		String repeatDay = "1111100";
		LocalTime repeatTime = LocalTime.of(13, 00);
		LocalDateTime startAt = LocalDateTime.of(2024, 9, 6, 13, 00);
		LocalDateTime endAt = LocalDateTime.of(2024, 9, 6, 13, 00);
		String tagImage = "image url";
		EventConfigRequestDto requestDto = new EventConfigRequestDto(title, description, repeatDay, repeatTime, startAt, endAt, tagImage);

		//when
		EventDto responseDto = adminService.configLotsEvent(requestDto);

		//then
		assertEquals(title, responseDto.getTitle());
		assertEquals(description, responseDto.getDescription());
		assertEquals(repeatDay, responseDto.getRepeatDay());
		assertEquals(repeatTime, responseDto.getRepeatTime());
		assertEquals(startAt, responseDto.getStartAt());
		assertEquals(endAt, responseDto.getEndAt());
		assertEquals(tagImage, responseDto.getTagImage());
	}

	@Test
	@Transactional
	@DisplayName("선착순 이벤트 보상 수정 service")
	void configArrivalEventReward() {
		//given
		Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id"));
		Page<ArrivalUser> arrivalUserPage = new PageImpl<>(arrivalUserList, pageable, arrivalUserList.size());
		when(arrivalUserRepository.findAll((Pageable) any())).thenReturn(arrivalUserPage);
		when(eventRepository.findById(1L)).thenReturn(Optional.of(arrivalEvent));
		when(arrivalUserRepository.findAll()).thenReturn(arrivalUserList);
		when(eventRewardRepository.findByEvent(arrivalEvent)).thenReturn(arrivalEvent.getEventRewardList());
		doAnswer(invocation -> {
			arrivalEvent.getEventRewardList().clear();
			return null;
		}).when(eventRewardRepository).deleteAllInBatch(Mockito.anyList());
		when(eventRewardRepository.saveAllAndFlush(any())).thenAnswer(new Answer<List<EventReward>>() {
			@Override
			public List<EventReward> answer(InvocationOnMock invocationOnMock) throws Throwable {
				List<EventReward> eventReward = (List<EventReward>) invocationOnMock.getArguments()[0];
				arrivalEvent.updateEventRewardList(eventReward);
				return null;
			}
		});
		int rewardNum = 5;

		List<EventRewardDto> eventRewardList = new ArrayList<>();
		for(int i = 1 ; i <= rewardNum ; i++) {
			EventRewardDto rewardDto = new EventRewardDto(i, i, "reward" + i);
			eventRewardList.add(rewardDto);
		}
		EventRewardConfigRequestDto requestDto = new EventRewardConfigRequestDto();
		requestDto.setEventRewardList(eventRewardList);

		//when
		EventRewardConfigResponseDto responseDto = adminService.configArrivalEventReward(requestDto);

		//then
		assertEquals(eventRewardList, responseDto.getEventRewards());

		ArrivalUserListDto arrivalUserListPageDto = (ArrivalUserListDto) responseDto.getUserListPage();
		for(int i = 0; i < arrivalUserListPageDto.getArrivalUserList().size(); i++){  //보상 확인
			ArrivalUserDto user = arrivalUserListPageDto.getArrivalUserList().get(i);
			int rank = user.getRank();

			int winSum = 0;
			for(EventRewardDto eventRewardDto : eventRewardList){
				winSum += eventRewardDto.getNumWinners();
				if(rank <= winSum){
					assertEquals(eventRewardDto.getReward(), user.getReward());
					break;
				}
			}
		}
	}

	@Test
	@Transactional
	@DisplayName("랜덤추첨 이벤트 보상 수정 service")
	void configLotsEventReward() {
		//given
		Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id"));
		Page<LotsUser> lotsUserPage = new PageImpl<>(lotsUserList, pageable, lotsUserList.size());
		when(lotsUserRepository.findAll((Pageable) any())).thenReturn(lotsUserPage);
		when(eventRepository.findById(2L)).thenReturn(Optional.of(lotsEvent));
		when(lotsUserRepository.findAll()).thenReturn(lotsUserList);
		when(eventRewardRepository.findByEvent(lotsEvent)).thenReturn(lotsEvent.getEventRewardList());
		doAnswer(invocation -> {
			lotsEvent.getEventRewardList().clear();
			return null;
		}).when(eventRewardRepository).deleteAllInBatch(Mockito.anyList());
		when(eventRewardRepository.saveAll(any())).thenAnswer(new Answer<List<EventReward>>() {
			@Override
			public List<EventReward> answer(InvocationOnMock invocationOnMock) throws Throwable {
				List<EventReward> eventRewards = (List<EventReward>) invocationOnMock.getArguments()[0];
				lotsEvent.updateEventRewardList(eventRewards);
				return null;
			}
		});
		int rewardNum = 5;
		List<EventRewardDto> eventRewardList = new ArrayList<>();
		for(int i = 1 ; i <= rewardNum ; i++) {
			EventRewardDto rewardDto = new EventRewardDto(i, i, "reward" + i);
			eventRewardList.add(rewardDto);
		}
		EventRewardConfigRequestDto requestDto = new EventRewardConfigRequestDto();
		requestDto.setEventRewardList(eventRewardList);

		EventWeightDto eventWeight = new EventWeightDto(3, "comment");
		requestDto.setEventWeight(eventWeight);


		//when
		EventRewardConfigResponseDto responseDto = adminService.configLotsEventReward(requestDto);

		//then
		assertEquals(eventRewardList, responseDto.getEventRewards());
		assertEquals(eventWeight, responseDto.getEventWeight());
	}

	@Test
	void getArrivalApplicationList() {
	}

	@Test
	void getLotsApplicationList() {
	}

	@Test
	void getLotsResult() {
		//given
		Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id"));
		Page<LotsUser> lotsUserPage = new PageImpl<>(lotsUserList, pageable, lotsUserList.size());
		when(eventRepository.findById(2L)).thenReturn(Optional.of(lotsEvent));
		when(lotsUserRepository.findAll()).thenReturn(lotsUserList);
		when(lotsUserRepository.findAll((Pageable) any())).thenReturn(lotsUserPage);

		//when
		LotsUserListDto responseDto = adminService.getLotsResult();

		//then
		List<EventReward> eventReward = lotsEvent.getEventRewardList();
		List<Integer> rewardCount = new ArrayList<>(Collections.nCopies(eventReward.size(), 0));
		for(LotsUserDto lotsUserDto : responseDto.getLotsUserList()){
			for(int i = 0; i < eventReward.size(); i++){
				if(lotsUserDto.getReward().equals(eventReward.get(i).getReward())){
					rewardCount.set(i, rewardCount.get(i) + 1);
					break;
				}
			}
		}
		for(int i = 0; i < eventReward.size(); i++){
			assertEquals(rewardCount.get(i), eventReward.get(i).getNumWinners());
		}
	}
}