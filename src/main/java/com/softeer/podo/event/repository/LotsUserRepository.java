package com.softeer.podo.event.repository;

import com.softeer.podo.event.model.entity.ArrivalUser;
import com.softeer.podo.event.model.entity.LotsUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LotsUserRepository extends JpaRepository<LotsUser, Long> {
    Optional<LotsUser> findByPhoneNum(String phoneNum);
    boolean existsByPhoneNum(String phoneNum);
    public Page<LotsUser> findAllByName(Pageable pageable, String name);
    public Page<LotsUser> findAllByPhoneNum(Pageable pageable, String phoneNum);
}
