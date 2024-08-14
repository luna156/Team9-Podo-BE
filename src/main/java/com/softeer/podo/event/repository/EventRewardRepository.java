package com.softeer.podo.event.repository;

import com.softeer.podo.event.model.entity.Event;
import com.softeer.podo.event.model.entity.EventReward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRewardRepository extends JpaRepository<EventReward, Long> {
	List<EventReward> findByEvent (Event event);
}
