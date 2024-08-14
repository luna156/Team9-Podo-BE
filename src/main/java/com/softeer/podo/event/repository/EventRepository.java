package com.softeer.podo.event.repository;

import com.softeer.podo.event.model.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
