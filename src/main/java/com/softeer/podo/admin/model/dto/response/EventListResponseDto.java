package com.softeer.podo.admin.model.dto.response;

import com.softeer.podo.admin.model.dto.EventDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EventListResponseDto {
	List<EventDto> eventList;
}
