package com.softeer.podo.admin.model.dto.response;

import com.softeer.podo.admin.model.dto.EventRewardDto;
import com.softeer.podo.admin.model.dto.EventWeightDto;
import com.softeer.podo.admin.model.dto.user.UserListDto;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EventRewardConfigResponseDto {
	private List<EventRewardDto> eventRewards;
	@Nullable
	private EventWeightDto eventWeight;
	private UserListDto userListPage;
}
