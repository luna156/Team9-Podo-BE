package com.softeer.podo.event.repository;

import com.softeer.podo.event.model.entity.LotsUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LotsUserRepository extends JpaRepository<LotsUser, Long> {
    Optional<LotsUser> findByPhoneNum(String phoneNum);
    boolean existsByPhoneNum(String phoneNum);
}
