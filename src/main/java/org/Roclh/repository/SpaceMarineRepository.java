package org.Roclh.repository;

import org.Roclh.model.SpaceMarine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SpaceMarineRepository extends JpaRepository<SpaceMarine, Long> {

    Page<SpaceMarine> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT sm FROM SpaceMarine sm ORDER BY sm.creationDate DESC LIMIT 1")
    SpaceMarine findTopByOrderByCreationDateDesc();

    @Query("SELECT COUNT(sm) FROM SpaceMarine sm WHERE sm.health > :health")
    Long countByHealthGreaterThan(@Param("health") Float health);

    @Modifying
    @Transactional
    @Query("DELETE FROM SpaceMarine sm WHERE sm.id = :id")
    int deleteMarineById(Long id);

    boolean existsByNameAndIdNot(String name, Long id);
}