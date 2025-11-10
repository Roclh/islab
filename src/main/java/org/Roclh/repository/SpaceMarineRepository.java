package org.Roclh.repository;

import org.Roclh.model.SpaceMarine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface SpaceMarineRepository extends JpaRepository<SpaceMarine, Long> {

    Page<SpaceMarine> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT sm FROM SpaceMarine sm ORDER BY sm.creationDate DESC LIMIT 1")
    SpaceMarine findTopByOrderByCreationDateDesc();

    @Query("SELECT sm.chapter, COUNT(sm) FROM SpaceMarine sm GROUP BY sm.chapter")
    List<Object[]> countByChapterGroup();

    @Query("SELECT COUNT(sm) FROM SpaceMarine sm WHERE sm.health > :health")
    Long countByHealthGreaterThan(@Param("health") Float health);

    boolean existsByNameAndIdNot(String name, Long id);
}