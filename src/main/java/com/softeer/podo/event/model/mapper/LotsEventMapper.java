package com.softeer.podo.event.model.mapper;

import com.softeer.podo.event.model.dto.response.LotsTypeResponseDto;
import com.softeer.podo.event.model.dto.KeyWordDto;
import com.softeer.podo.event.model.dto.ScenarioDto;
import com.softeer.podo.event.model.dto.WordCloudResponseDto;
import com.softeer.podo.event.model.entity.KeyWord;
import com.softeer.podo.event.model.entity.TestResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class LotsEventMapper {

	public LotsTypeResponseDto TestResultToApplicationDto (TestResult testResult) {
		LotsTypeResponseDto dto = new LotsTypeResponseDto();
        dto.setResultId(testResult.getId());
		dto.setResult(testResult.getResult());
		dto.setDescription(testResult.getDescription());
		dto.setType(testResult.getType());

		ArrayList<ScenarioDto> scenarioArrayList =  new ArrayList<>();
		scenarioArrayList.add( new ScenarioDto(testResult.getScenario1(), testResult.getSubtitle1(), testResult.getImage1()));
		scenarioArrayList.add( new ScenarioDto(testResult.getScenario2(), testResult.getSubtitle2(), testResult.getImage2()));
		scenarioArrayList.add( new ScenarioDto(testResult.getScenario3(), testResult.getSubtitle3(), testResult.getImage3()));
		dto.setScenarioList(scenarioArrayList);
		return dto;
	}

	public WordCloudResponseDto KeyWordListToWordCloudResponseDto (List<KeyWord> keyWordList) {
		List<KeyWordDto> keyWordDtoList = new ArrayList<>();
		for (KeyWord keyWord : keyWordList) {
			KeyWordDto keyWordDto = new KeyWordDto(keyWord.getKeyword(), keyWord.getCount());
			keyWordDtoList.add(keyWordDto);
		}
		keyWordDtoList.sort(Comparator.comparingInt(KeyWordDto::getCount).reversed());
		return new WordCloudResponseDto(keyWordDtoList);
	}
}
