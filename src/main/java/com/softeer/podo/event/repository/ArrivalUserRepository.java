package com.softeer.podo.event.repository;

import com.softeer.podo.event.model.entity.ArrivalUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArrivalUserRepository extends JpaRepository<ArrivalUser, Long> {
	Page<ArrivalUser> findAllByNameLike(Pageable pageable, String name);
	Page<ArrivalUser> findAllByPhoneNumLike(Pageable pageable, String phoneNum);
}
