package com.softeer.podo.event.repository;

import com.softeer.podo.event.model.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTypeRepository extends JpaRepository<EventType, Long> {
}
