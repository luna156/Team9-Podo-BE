package com.softeer.podo.admin.model.dto.mapper;

import com.softeer.podo.admin.model.dto.EventDto;
import com.softeer.podo.admin.model.dto.EventListResponseDto;
import com.softeer.podo.admin.model.dto.EventRewardDto;
import com.softeer.podo.admin.model.dto.EventWeightDto;
import com.softeer.podo.admin.model.entity.Event;
import com.softeer.podo.admin.model.entity.EventReward;
import com.softeer.podo.admin.model.entity.EventWeight;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class EventMapper {
	public static EventDto EventToEventDto(Event event) {
		List<EventRewardDto> eventRewardDtoList = new ArrayList<>();
		event.getEventRewardList().forEach(eventReward -> eventRewardDtoList.add(
				new EventRewardDto(
						eventReward.getRewardRank(),
						eventReward.getNumWinners(),
						eventReward.getReward()
				)
		));
		return EventDto.builder()
				.id(event.getId())
				.eventType(event.getEventType().getType())
				.title(event.getTitle())
				.description(event.getDescription())
				.repeatDay(event.getRepeatDay())
				.repeatTime(event.getRepeatTime())
				.startAt(event.getStartAt())
				.endAt(event.getEndAt())
				.tagImage(event.getTagImage())
				.eventRewardList(eventRewardDtoList)
				.eventWeight(
						(event.getEventWeight() == null)?
								null :
								new EventWeightDto(
										event.getEventWeight().getTimes(),
										event.getEventWeight().getWeightCondition()
								)
				)
				.build();
	}

	public static EventListResponseDto eventListToEventListResponseDto(List<Event> eventList){
		List<EventDto> eventDtoList = new ArrayList<>();
		eventList.forEach(event -> eventDtoList.add(EventToEventDto(event)));
		return new EventListResponseDto(eventDtoList);
	}

	public static EventRewardDto eventRewardToEventRewardDto(EventReward eventReward){
		return new EventRewardDto(eventReward.getRewardRank(), eventReward.getNumWinners(), eventReward.getReward());
	}

	public static EventWeightDto eventWeightToEventWeightDto(EventWeight eventWeight){
		return new EventWeightDto(eventWeight.getTimes(), eventWeight.getWeightCondition());
	}
}
