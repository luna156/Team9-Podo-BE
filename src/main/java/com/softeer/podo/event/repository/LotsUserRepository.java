package com.softeer.podo.event.repository;

import com.softeer.podo.event.model.entity.ArrivalUser;
import com.softeer.podo.event.model.entity.LotsUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface LotsUserRepository extends JpaRepository<LotsUser, Long> {
    Optional<LotsUser> findByPhoneNum(String phoneNum);
    boolean existsByPhoneNum(String phoneNum);
    default Page<LotsUser> findAllByNameLikeAndCreatedAt(Pageable pageable, String name, LocalDate createdAt){
        if(createdAt == null){
            return findAllByNameLike(pageable, name);
        }
        LocalDateTime from = createdAt.atStartOfDay();
        LocalDateTime to = from.plusDays(1).minusNanos(1);
        return findAllByNameLikeAndCreatedAtBetween(pageable, name, from, to);
    }
    default Page<LotsUser> findAllByPhoneNumLikeAndCreatedAt(Pageable pageable, String phoneNum, LocalDate createdAt){
        if(createdAt == null){
            return findAllByPhoneNumLike(pageable, phoneNum);
        }
        LocalDateTime from = createdAt.atStartOfDay();
        LocalDateTime to = from.plusDays(1).minusNanos(1);
        return findAllByPhoneNumLikeAndCreatedAtBetween(pageable, phoneNum, from, to);
    }
    default Page<LotsUser> findAllByCreatedAt(Pageable pageable, LocalDate createdAt){
        if(createdAt == null){
            return findAll(pageable);
        }
        LocalDateTime from = createdAt.atStartOfDay();
        LocalDateTime to = from.plusDays(1).minusNanos(1);
        return findAllByCreatedAtBetween(pageable, from, to);
    }
    Page<LotsUser> findAllByNameLikeAndCreatedAtBetween(Pageable pageable, String name, LocalDateTime from, LocalDateTime to);
    Page<LotsUser> findAllByPhoneNumLikeAndCreatedAtBetween(Pageable pageable, String phoneNum, LocalDateTime from, LocalDateTime to);
    Page<LotsUser> findAllByNameLike(Pageable pageable, String name);
    Page<LotsUser> findAllByPhoneNumLike(Pageable pageable, String phoneNum);
    Page<LotsUser> findAllByCreatedAtBetween(Pageable pageable, LocalDateTime from, LocalDateTime to);
}
