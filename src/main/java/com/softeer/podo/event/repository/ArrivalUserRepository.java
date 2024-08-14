package com.softeer.podo.event.repository;

import com.softeer.podo.event.model.entity.ArrivalUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArrivalUserRepository extends JpaRepository<ArrivalUser, Long> {
}
