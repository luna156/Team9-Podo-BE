package com.softeer.podo.admin.service;


import com.softeer.podo.admin.model.dto.*;
import com.softeer.podo.admin.model.dto.user.ArrivalUserDto;
import com.softeer.podo.admin.model.dto.user.ArrivalUserListDto;
import com.softeer.podo.admin.model.dto.mapper.AdminMapper;
import com.softeer.podo.admin.model.dto.mapper.UserMapper;
import com.softeer.podo.admin.model.dto.user.LotsUserListDto;
import com.softeer.podo.admin.model.entity.Event;
import com.softeer.podo.admin.model.entity.EventReward;
import com.softeer.podo.admin.model.exception.EventNotFoundException;
import com.softeer.podo.admin.repository.EventRepository;
import com.softeer.podo.admin.repository.EventRewardRepository;
import com.softeer.podo.admin.repository.EventWeightRepository;
import com.softeer.podo.user.repository.ArrivalUserRepository;
import com.softeer.podo.user.repository.LotsUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final EventRepository eventRepository;
	private final EventRewardRepository eventRewardRepository;
	private final EventWeightRepository eventWeightRepository;
	private final AdminMapper adminMapper;
	private final LotsUserRepository lotsUserRepository;
	private final ArrivalUserRepository arrivalUserRepository;
	private final UserMapper userMapper;

	@Transactional
	public EventListResponseDto getEventList() {
		return adminMapper.eventListToEventListResponseDto(eventRepository.findAll());
	}

	@Transactional
	public EventDto configArrivalEvent(EventConfigRequestDto dto) {
		Optional<Event> optionalArrivalEvent = eventRepository.findById(1L);
		if(optionalArrivalEvent.isEmpty()){
			throw new EventNotFoundException();
		}
		Event arrivalEvent = optionalArrivalEvent.get();

		arrivalEvent.setTitle(dto.getTitle());
		arrivalEvent.setDescription(dto.getDescription());
		arrivalEvent.setRepeatDay(dto.getRepeatDay());
		arrivalEvent.setRepeatTime(dto.getRepeatTime());
		arrivalEvent.setStartAt(dto.getStartAt());
		arrivalEvent.setEndAt(dto.getEndAt());
		arrivalEvent.setTagImage(dto.getTagImage());

		eventRepository.save(arrivalEvent);
		return adminMapper.EventToEventDto(arrivalEvent);
	}

	@Transactional
	public EventDto configLotsEvent(EventConfigRequestDto dto) {
		Optional<Event> optionalLotslEvent = eventRepository.findById(2L);
		if(optionalLotslEvent.isEmpty()){
			throw new EventNotFoundException();
		}
		Event lotsEvent = optionalLotslEvent.get();

		lotsEvent.setTitle(dto.getTitle());
		lotsEvent.setDescription(dto.getDescription());
		lotsEvent.setRepeatDay(dto.getRepeatDay());
		lotsEvent.setRepeatTime(dto.getRepeatTime());
		lotsEvent.setStartAt(dto.getStartAt());
		lotsEvent.setEndAt(dto.getEndAt());
		lotsEvent.setTagImage(dto.getTagImage());

		eventRepository.save(lotsEvent);
		return adminMapper.EventToEventDto(lotsEvent);
	}

	@Transactional
	public ArrivalUserListDto configArrivalEventReward(EventRewardConfigRequestDto dto) {
		Optional<Event> optionalArrivalEvent = eventRepository.findById(1L);
		if(optionalArrivalEvent.isEmpty()){
			throw new EventNotFoundException();
		}
		Event arrivalEvent = optionalArrivalEvent.get();

		List<EventReward> arrivalReward = eventRewardRepository.findByEvent(arrivalEvent);
		eventRewardRepository.deleteAll(arrivalReward);
		eventRewardRepository.flush(); // 즉시 데이터베이스에 반영

		for(EventRewardDto rewardDto : dto.getEventRewardList()) {
			eventRewardRepository.save(
					EventReward.builder()
							.event(arrivalEvent)
							.reward(rewardDto.getReward())
							.rewardRank(rewardDto.getRank())
							.numWinners(rewardDto.getNumWinners())
							.build()
			);
		}
		return getArrivalApplicationList();
	}

	@Transactional
	public LotsUserListDto configLotsEventReward(EventRewardConfigRequestDto dto) {
		Optional<Event> optionalLotsEvent = eventRepository.findById(2L);
		if(optionalLotsEvent.isEmpty()){
			throw new EventNotFoundException();
		}
		Event lotsEvent = optionalLotsEvent.get();

		List<EventReward> lotsReward = eventRewardRepository.findByEvent(lotsEvent);
		eventRewardRepository.deleteAll(lotsReward);
		eventRewardRepository.flush(); // 즉시 데이터베이스에 반영

		for(EventRewardDto rewardDto : dto.getEventRewardList()) {
			eventRewardRepository.save(
					EventReward.builder()
							.event(lotsEvent)
							.reward(rewardDto.getReward())
							.rewardRank(rewardDto.getRank())
							.numWinners(rewardDto.getNumWinners())
							.build()
			);
		}

		lotsEvent.getEventWeight().setWeightCondition(dto.getEventWeight().getCondition());
		lotsEvent.getEventWeight().setTimes(dto.getEventWeight().getTimes());
		eventWeightRepository.save(lotsEvent.getEventWeight());

		return getLotsApplicationList();
	}

	@Transactional
	public ArrivalUserListDto getArrivalApplicationList() {
		ArrivalUserListDto arrivalUserListDto = userMapper.ArrivalUserListToArrivalUserListDto(arrivalUserRepository.findAll());
		//선착순 이벤트 id
		Optional<Event> arrivalEvent = eventRepository.findById(1L);
		if(arrivalEvent.isEmpty()){
			throw new EventNotFoundException();
		}
		List<EventReward> eventRewardList = arrivalEvent.get().getEventRewardList();
		// 보상 순위 기준으로 정렬
		eventRewardList.sort(Comparator.comparingInt(EventReward::getRewardRank));

		for(ArrivalUserDto arrivalUserDto : arrivalUserListDto.getApplicationList()){
			int base = 0; //누적 등수
			for (EventReward eventReward : eventRewardList) {
				//해당 상품을 받을 수 있는 등수이면
				if (arrivalUserDto.getRank() - base <= eventReward.getNumWinners()) {
					arrivalUserDto.setReward(eventReward.getReward());
					break;
				}else arrivalUserDto.setReward("");
				base += eventReward.getNumWinners();
			}
		}
		return arrivalUserListDto;
	}

	@Transactional
	public LotsUserListDto getLotsApplicationList() {
		LotsUserListDto lotsUserListDto = userMapper.LotsUserListToLotsUserListDto(lotsUserRepository.findAll());
		return lotsUserListDto;
	}
}
