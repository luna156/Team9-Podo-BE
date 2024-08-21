package com.softeer.podo.event.repository;

import com.softeer.podo.event.model.entity.ArrivalUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ArrivalUserRepository extends JpaRepository<ArrivalUser, Long> {
	default Page<ArrivalUser> findAllByNameLikeAndCreatedAt(Pageable pageable, String name, LocalDate createdAt){
		if(createdAt == null){
			return findAllByNameLike(pageable, name);
		}
		LocalDateTime from = createdAt.atStartOfDay();
		LocalDateTime to = from.plusDays(1).minusNanos(1);
		return findAllByNameLikeAndCreatedAtBetween(pageable, name, from, to);
	}
	default Page<ArrivalUser> findAllByPhoneNumLikeAndCreatedAt(Pageable pageable, String phoneNum, LocalDate createdAt){
		if(createdAt == null){
			return findAllByPhoneNumLike(pageable, phoneNum);
		}
		LocalDateTime from = createdAt.atStartOfDay();
		LocalDateTime to = from.plusDays(1).minusNanos(1);
		return findAllByPhoneNumLikeAndCreatedAtBetween(pageable, phoneNum, from, to);
	}
	default Page<ArrivalUser> findAllByCreatedAt(Pageable pageable, LocalDate createdAt){
		if(createdAt == null){
			return findAll(pageable);
		}
		LocalDateTime from = createdAt.atStartOfDay();
		LocalDateTime to = from.plusDays(1).minusNanos(1);
		return findAllByCreatedAtBetween(pageable, from, to);
	}
	Page<ArrivalUser> findAllByNameLikeAndCreatedAtBetween(Pageable pageable, String name, LocalDateTime from, LocalDateTime to);
	Page<ArrivalUser> findAllByPhoneNumLikeAndCreatedAtBetween(Pageable pageable, String phoneNum, LocalDateTime from, LocalDateTime to);
	Page<ArrivalUser> findAllByNameLike(Pageable pageable, String name);
	Page<ArrivalUser> findAllByPhoneNumLike(Pageable pageable, String phoneNum);
	Page<ArrivalUser> findAllByCreatedAtBetween(Pageable pageable, LocalDateTime from, LocalDateTime to);
}
