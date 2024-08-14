package com.softeer.podo.event.repository;

import com.softeer.podo.event.model.entity.LotsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import com.softeer.podo.event.model.entity.LotsComment;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LotsCommentRepository extends JpaRepository<LotsComment, Long> {
    boolean existsByLotsUser(LotsUser lotsUser);

    List<LotsComment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
