package com.softeer.podo.admin.model.dto.mapper;

import com.softeer.podo.admin.model.dto.EventDto;
import com.softeer.podo.admin.model.dto.EventListResponseDto;
import com.softeer.podo.admin.model.dto.EventRewardDto;
import com.softeer.podo.admin.model.dto.EventWeightDto;
import com.softeer.podo.admin.model.entity.Event;
import com.softeer.podo.admin.model.entity.EventReward;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdminMapper {

	public EventListResponseDto eventListToEventListResponseDto(List<Event> eventList){

		List<EventDto> eventDtoList = new ArrayList<>();
		for (Event event : eventList) {
			List<EventRewardDto> eventRewardDtoList = new ArrayList<>();
			for(EventReward eventReward : event.getEventRewardList()){
				eventRewardDtoList.add(
						new EventRewardDto(
								eventReward.getRewardRank(),
								eventReward.getNumWinners(),
								eventReward.getReward()
						)
				);
			}


			eventDtoList.add(
					EventDto.builder()
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
									new EventWeightDto(
											event.getEventWeight().getTimes(),
											event.getEventWeight().getWeightCondition()
									)
							)
							.build()
			);
		}

		return new EventListResponseDto(eventDtoList);
	}
}
