package com.softeer.podo.event.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordCloudResponseDto {
	private List<KeyWordDto> wordList;
}
