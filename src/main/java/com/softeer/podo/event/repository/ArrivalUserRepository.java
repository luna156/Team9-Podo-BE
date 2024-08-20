package com.softeer.podo.event.repository;

import com.softeer.podo.event.model.entity.ArrivalUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArrivalUserRepository extends JpaRepository<ArrivalUser, Long> {
	Page<ArrivalUser> findAllByName(Pageable pageable, String name);
	Page<ArrivalUser> findAllByPhoneNum(Pageable pageable, String phoneNum);
}
