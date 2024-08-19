package com.softeer.podo.event.scheduler;

import com.softeer.podo.event.model.entity.KeyWord;
import com.softeer.podo.event.model.entity.LotsComment;
import com.softeer.podo.event.model.entity.LotsUser;
import com.softeer.podo.event.repository.KeyWordRepository;
import com.softeer.podo.event.repository.LotsCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class CommentCheckSchedulerTest {

	@MockBean
	LotsCommentRepository lotsCommentRepository;

	@MockBean
	KeyWordRepository keyWordRepository;

	@Autowired
	CommentCheckScheduler commentCheckScheduler;

	private List<LotsComment> comments;
	private List<KeyWord> keyWords;

	@BeforeEach
	void setUp() {
		comments = new ArrayList<>();
		keyWords = new ArrayList<>();
		for (int i = 1; i <= 9; i++) {
			LotsUser lotsUser = new LotsUser();
			LotsComment lotsComment = new LotsComment((long) i, lotsUser, "keyword" + i);
			comments.add(lotsComment);
			keyWords.add(new KeyWord((long) i, "keyword" + i, i));
		}
	}

	@Test
	void setEventArrivalCount() {
		//given
		Mockito.when(lotsCommentRepository.findByCreatedAtBetween(any(), any())).thenReturn(comments);
		Mockito.when(keyWordRepository.findAll()).thenReturn(keyWords);

		//when
		commentCheckScheduler.setEventArrivalCount();

		//then
		for (KeyWord keyWord : keyWords) {
			assertEquals(keyWord.getCount(), keyWord.getId() + 1L);
		}
	}
}