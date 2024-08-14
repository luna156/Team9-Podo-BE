package com.softeer.podo.event.scheduler;


import com.softeer.podo.event.model.entity.KeyWord;
import com.softeer.podo.event.model.entity.LotsComment;
import com.softeer.podo.event.repository.KeyWordRepository;
import com.softeer.podo.event.repository.LotsCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentCheckScheduler {

	private final KeyWordRepository keywordRepository;
	private final LotsCommentRepository lotsCommentRepository;

	/**
	 * 특정 시간에 comment하루동안 등록된 comment들에 대해서 키워드 빈도수 체크 실행
	 */
	@Scheduled(cron = "0 0 03 * * *")
	public void setEventArrivalCount() {
		// 어제 일자의 comment들 수집
		LocalDateTime startOfDay = LocalDate.now().minusDays(1).atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

		List<LotsComment> comments = lotsCommentRepository.findByCreatedAtBetween(startOfDay, endOfDay).orElse(new ArrayList<>());
		if(comments.isEmpty()) return;

		List<KeyWord> keyWords = keywordRepository.findAll();
		for(LotsComment comment : comments) {
			for(KeyWord keyWord : keyWords) {
				if(comment.getComment().contains(keyWord.getKeyword())){
					keyWord.increaseCount();
				}
			}
		}
	}
}
