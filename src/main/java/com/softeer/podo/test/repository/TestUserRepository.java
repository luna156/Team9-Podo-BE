package com.softeer.podo.test.repository;

import com.softeer.podo.event.model.entity.TestUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestUserRepository extends JpaRepository<TestUser, Long> {

}
