package com.softeer.podo.admin.service;


import com.amazonaws.services.s3.AmazonS3;
import com.softeer.podo.admin.exception.S3RegisterFailureException;
import com.softeer.podo.admin.exception.EventNotFoundException;
import com.softeer.podo.admin.model.dto.*;
import com.softeer.podo.admin.model.dto.request.ConfigEventRequestDto;
import com.softeer.podo.admin.model.dto.request.ConfigEventRewardRequestDto;
import com.softeer.podo.admin.model.dto.response.ConfigEventRewardResponseDto;
import com.softeer.podo.admin.model.dto.response.EventListResponseDto;
import com.softeer.podo.admin.model.mapper.EventMapper;
import com.softeer.podo.admin.model.mapper.UserMapper;
import com.softeer.podo.admin.model.dto.LotsUserListDto;
import com.softeer.podo.common.utils.S3Utils;
import com.softeer.podo.event.model.entity.ArrivalUser;
import com.softeer.podo.event.model.entity.Event;
import com.softeer.podo.event.model.entity.EventReward;
import com.softeer.podo.event.model.entity.LotsUser;
import com.softeer.podo.event.repository.ArrivalUserRepository;
import com.softeer.podo.event.repository.EventRepository;
import com.softeer.podo.event.repository.EventRewardRepository;
import com.softeer.podo.event.repository.LotsUserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final EventRepository eventRepository;
	private final LotsUserRepository lotsUserRepository;
	private final ArrivalUserRepository arrivalUserRepository;
	private final EventRewardRepository eventRewardRepository;

	private final Long ARRIVAL_EVENT_ID = 1L;
	private final Long LOTS_EVENT_ID = 2L;
	private final int PAGE_SIZE = 10;

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

	@Transactional
	public EventListResponseDto getEventList() {
		return EventMapper.eventListToEventListResponseDto(eventRepository.findAll());
	}

    /**
     * 이미지 파일을 받아서 S3에 업로드하고, 선착순 이벤트 정보를 수정한 후 정보를 반환해준다.
     * @param dto 이벤트 업로드 정보
     * @param image 이벤트에 업로드할 이미지 파일
     * @return 업로드된 이벤트 정보
     * @throws S3RegisterFailureException s3 저장 오류
     */
	@Transactional
	public EventDto configArrivalEvent(ConfigEventRequestDto dto, MultipartFile image) {
		Event arrivalEvent = updateEventByConfigDto(ARRIVAL_EVENT_ID, dto);

        String imageUri = null;
        if(image!=null) { // 이미지가 null이 아닌 경우 s3 업로드
            try {
                imageUri = S3Utils.saveFile(amazonS3, bucket, image);
            } catch (IOException e) {
                throw new S3RegisterFailureException("리뷰 이미지 저장 중 오류가 발생했습니다.");
            }
        }
        arrivalEvent.updateTagImageLink(imageUri);

		return EventMapper.EventToEventDto(arrivalEvent);
	}

    /**
     * 이미지 파일을 받아서 S3에 업로드하고, 랜덤 이벤트 정보를 수정한 후 정보를 반환해준다.
     * @param dto 이벤트 업로드 정보
     * @param image 이벤트에 업로드할 이미지 파일
     * @return 업로드된 이벤트 정보
     * @throws S3RegisterFailureException s3 저장 오류
     */
	@Transactional
	public EventDto configLotsEvent(ConfigEventRequestDto dto, MultipartFile image) {
		Event lotsEvent = updateEventByConfigDto(LOTS_EVENT_ID, dto);

        String imageUri = null;
        if(image!=null) { // 이미지가 null이 아닌 경우 s3 업로드
            try {
                imageUri = S3Utils.saveFile(amazonS3, bucket, image);
            } catch (IOException e) {
                throw new S3RegisterFailureException("리뷰 이미지 저장 중 오류가 발생했습니다.");
            }
        }
        lotsEvent.updateTagImageLink(imageUri);

		return EventMapper.EventToEventDto(lotsEvent);
	}

	/**
	 * 선착순 이벤트 상품 수정 api
	 * @param dto 상품 리스트 정보가 담겨있는 dto
	 * @return 수정된 상품 리스트 및 그에따른 사용자 list
	 * @throws EventNotFoundException 500 에러 - 이벤트를 찾을 수 없음
	 */
	@Transactional
	public ConfigEventRewardResponseDto configArrivalEventReward(ConfigEventRewardRequestDto dto) {
		Event arrivalEvent = eventRepository.findById(ARRIVAL_EVENT_ID).orElseThrow(EventNotFoundException::new);
		List<EventReward> arrivalRewards = eventRewardRepository.findByEvent(arrivalEvent);
		eventRewardRepository.deleteAllInBatch(arrivalRewards);

		List<EventReward> lotsRewardList = new ArrayList<>();
		for(EventRewardDto rewardDto : dto.getEventRewardList()) {
			lotsRewardList.add(
					EventReward.builder().event(arrivalEvent)
							.reward(rewardDto.getReward())
							.rewardRank(rewardDto.getRank())
							.numWinners(rewardDto.getNumWinners())
							.build());

		}
		eventRewardRepository.saveAllAndFlush(lotsRewardList);

		List<EventRewardDto> eventRewardDtoList =
				lotsRewardList.stream()
						.map(EventMapper::eventRewardToEventRewardDto)
						.toList();

		return new ConfigEventRewardResponseDto(eventRewardDtoList, null, getArrivalApplicationList(0,null, null, null));
	}

	/**
	 * 선착순 이벤트 상품 수정 api
	 * @param dto 상품 리스트 및 가중치 정보가 담겨있는 dto
	 * @return 수정된 상품 리스트 및 가중치 정보, 그리고 그에따른 사용자 list
	 * @throws EventNotFoundException 500 에러 - 이벤트를 찾을 수 없음
	 */
	@Transactional
	public ConfigEventRewardResponseDto configLotsEventReward(ConfigEventRewardRequestDto dto) {
		Event lotsEvent = eventRepository.findById(LOTS_EVENT_ID).orElseThrow(EventNotFoundException::new);
		List<EventReward> lotsRewards = eventRewardRepository.findByEvent(lotsEvent);
		eventRewardRepository.deleteAllInBatch(lotsRewards);

		List<EventReward> lotsRewardList = new ArrayList<>();
		for(EventRewardDto rewardDto : dto.getEventRewardList()) {
			lotsRewardList.add(
					EventReward.builder()
							.event(lotsEvent)
							.reward(rewardDto.getReward())
							.rewardRank(rewardDto.getRank())
							.numWinners(rewardDto.getNumWinners())
							.build()
			);
		}
		eventRewardRepository.saveAll(lotsRewardList);
		lotsEvent.getEventWeight().updateWeightCondition(dto.getEventWeight().getCondition());
		lotsEvent.getEventWeight().updateTimes(dto.getEventWeight().getTimes());
		eventRewardRepository.flush(); // 즉시 데이터베이스에 반영

		List<EventRewardDto> eventRewardDtoList =
				lotsRewardList.stream()
						.map(EventMapper::eventRewardToEventRewardDto)
						.toList();
		EventWeightDto eventWeightDto = EventMapper.eventWeightToEventWeightDto(lotsEvent.getEventWeight());


		getLotsResult();

		return new ConfigEventRewardResponseDto(eventRewardDtoList, eventWeightDto, getLotsApplicationList(0, null, null, null));
	}

	/**
	 * 이벤트 상품이 적용된 선착순 사용자 목록 반환
	 * @param pageNo 페이지 번호
	 * @param name 사용자 이름
	 * @param phoneNum 사용자 전화번호
	 * @param createdAtString 생성된 날짜 문자열
	 * @return 선착순 사용자 목록
	 * @throws EventNotFoundException 500 에러 - 이벤트를 찾을 수 없음
	 */
	@Transactional
	public ArrivalUserListDto getArrivalApplicationList(int pageNo, String name, String phoneNum, String createdAtString) {
		// 형식 체크
		if (createdAtString != null && !createdAtString.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
			throw new ValidationException("날짜는 해당 형식이어야 합니다: YYYY-MM-DD");
		}

		// string으로 들어온 createdAt을 변환
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate createdAt = null;
		if(createdAtString != null) {
			createdAt = LocalDate.parse(createdAtString, formatter);
		}

		// page 생성
		Page<ArrivalUser> page;
		Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

		// 들어온 url parameter 기반으로 검색
		if(name!=null){
			page = arrivalUserRepository.findAllByNameLikeAndCreatedAt(pageable, "%" + name + "%", createdAt);
		}else if(phoneNum!=null){
			page = arrivalUserRepository.findAllByPhoneNumLikeAndCreatedAt(pageable, "%" + phoneNum + "%", createdAt);
		}else page = arrivalUserRepository.findAllByCreatedAt(pageable, createdAt);

		ArrivalUserListDto arrivalUserListDto = UserMapper.ArrivalUserPageToArrivalUserListDto(page);
		//선착순 이벤트 id
		Event arrivalEvent = eventRepository.findById(ARRIVAL_EVENT_ID).orElseThrow(EventNotFoundException::new);
		List<EventReward> eventRewardList = arrivalEvent.getEventRewardList();
		// 보상 순위 기준으로 정렬
		eventRewardList.sort(Comparator.comparingInt(EventReward::getRewardRank));

		for (ArrivalUserDto arrivalUserDto : arrivalUserListDto.getArrivalUserList()) {
			int base = 0; //누적 등수
			for (EventReward eventReward : eventRewardList) {
				//해당 상품을 받을 수 있는 등수이면
				if (arrivalUserDto.getRank() - base <= eventReward.getNumWinners()) {
					arrivalUserDto.setReward(eventReward.getReward());
					break;
				} else arrivalUserDto.setReward("");
				base += eventReward.getNumWinners();
			}
		}
		return arrivalUserListDto;
	}

	/**
	 * 추첨을 진행한 랜덤 추첨 사용자 목록 반환
	 * @param pageNo 페이지 번호
	 * @param name 사용자 이름
	 * @param phoneNum 사용자 전화번호
	 * @param createdAtString 생성된 날짜 문자열
	 * @return 랜덤 추첨 사용자 목록
	 * @throws EventNotFoundException 500 에러 - 이벤트를 찾을 수 없음
	 */
	@Transactional
	public LotsUserListDto getLotsApplicationList(int pageNo, String name, String phoneNum, String createdAtString) {
		// 형식 체크
		if (createdAtString != null && !createdAtString.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
			throw new ValidationException("날짜는 해당 형식이어야 합니다: YYYY-MM-DD");
		}

		// string으로 들어온 createdAt을 변환
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate createdAt = null;
		if(createdAtString != null) {
			createdAt = LocalDate.parse(createdAtString, formatter);
		}

		// page 생성
		Page<LotsUser> page;
		Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

		// 들어온 url parameter 기반으로 검색
		if(name!=null){
			page = lotsUserRepository.findAllByNameLikeAndCreatedAt(pageable, "%" + name + "%", createdAt);
		}else if(phoneNum!=null){
			page = lotsUserRepository.findAllByPhoneNumLikeAndCreatedAt(pageable, "%" + phoneNum + "%", createdAt);
		}else page = lotsUserRepository.findAllByCreatedAt(pageable, createdAt);

		return UserMapper.LotsUserPageToLotsUserListDto(page);
	}


	/**
	 * 추첨을 진행한 랜덤 추첨 사용자 목록 반환 (반환기록 사용 x)
	 * @return 랜덤 추첨 사용자 목록
	 * @throws EventNotFoundException 500 에러 - 이벤트를 찾을 수 없음
	 */
	@Transactional
	public LotsUserListDto getLotsResult() {
		//랜덤 추첨 이벤트
		Event lotsEvent = eventRepository.findById(LOTS_EVENT_ID).orElseThrow(EventNotFoundException::new);
		//보상 리스트
		List<EventReward> eventRewardList = lotsEvent.getEventRewardList();
		//응모 목록
		List<LotsUser> lotsUserList = lotsUserRepository.findAll();

		//comment에 대한 가중치
		int weight = lotsEvent.getEventWeight().getTimes();
		//전체 가중치합
		int totalWeight = 0;
		for (LotsUser lotsUser : lotsUserList) {
			if (lotsUser.getLotsComment() != null) {
				totalWeight += weight;
			} else totalWeight++;
		}

		ArrayList<Boolean> userCheckList = new ArrayList<>(Collections.nCopies(lotsUserList.size(), false));
		eventRewardList.sort(Comparator.comparingInt(EventReward::getRewardRank));
		for (EventReward eventReward : eventRewardList) {
			if(totalWeight <=0) break;
			//해당 reward 추첨
			for (int winCount = 0; winCount < eventReward.getNumWinners() && !lotsUserList.isEmpty(); winCount++) {
				long currentTimeMillis = System.currentTimeMillis();
				Random random = new Random(currentTimeMillis);
				int randomInt = random.nextInt(totalWeight); // 랜덤 정수

				int weightSum = 0;
				for (int i = 0; i < lotsUserList.size(); i++) {
					if (userCheckList.get(i)) {
						continue;
					}
					LotsUser lotsUser = lotsUserList.get(i);
					weightSum += lotsUser.getLotsComment() != null ? weight : 1;
					if (randomInt < weightSum) {
						lotsUser.setReward(eventReward.getReward());
						lotsUserRepository.save(lotsUser);
						userCheckList.set(i, true);
						totalWeight -= lotsUser.getLotsComment() != null ? weight : 1;
						break;
					}
				}
			}
		}

		for (int i = 0; i < lotsUserList.size(); i++) {
			if (userCheckList.get(i)) {
				continue;
			}
			lotsUserList.get(i).setReward("");
			lotsUserRepository.save(lotsUserList.get(i));
		}

		return getLotsApplicationList(0, null, null, null);
	}

	private Event updateEventByConfigDto(Long eventId, ConfigEventRequestDto dto) {
		Event event = eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);
		event.updateEvent(
				dto.getTitle(),
				dto.getDescription(),
				dto.getRepeatDay(),
				dto.getRepeatTime(),
				dto.getStartAt(),
				dto.getEndAt()
		);
		return event;
	}
}
