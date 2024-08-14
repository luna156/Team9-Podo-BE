package com.softeer.podo.event.repository;

import com.softeer.podo.event.model.entity.KeyWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyWordRepository extends JpaRepository<KeyWord, Long> {
}
