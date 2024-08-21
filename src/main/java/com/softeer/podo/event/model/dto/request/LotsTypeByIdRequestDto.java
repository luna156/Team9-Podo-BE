package com.softeer.podo.event.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LotsTypeByIdRequestDto {
	@Min(value = 1, message = "결과 id가 잘못되었습니다.")
	@Max(value = 4, message = "결과 id가 잘못되었습니다.")
	private Long resultTypeId;
}
