package org.Roclh.repository;

import org.Roclh.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Optional<Chapter> findByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
