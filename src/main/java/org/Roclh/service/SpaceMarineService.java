package org.Roclh.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.model.Chapter;
import org.Roclh.model.SpaceMarine;
import org.Roclh.repository.SpaceMarineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpaceMarineService {

    private final SpaceMarineRepository spaceMarineRepository;
    private final WebSocketService webSocketService;

    public Page<SpaceMarine> findAll(Pageable pageable) {
        return spaceMarineRepository.findAll(pageable);
    }

    public Page<SpaceMarine> findByNameContaining(String name, Pageable pageable) {
        return spaceMarineRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Optional<SpaceMarine> findById(Long id) {
        return spaceMarineRepository.findById(id);
    }

    public SpaceMarine save(SpaceMarine spaceMarine) {
        SpaceMarine saved = spaceMarineRepository.save(spaceMarine);
        log.info("Space Marine saved, sending WebSocket notification");
        webSocketService.notifySpaceMarineUpdate();
        return saved;
    }

    public void deleteById(Long id) {
        spaceMarineRepository.deleteById(id);
        log.info("Space Marine deleted, sending WebSocket notification");
        webSocketService.notifySpaceMarineUpdate();
    }

    public SpaceMarine findLatest() {
        try {
            return spaceMarineRepository.findTopByOrderByCreationDateDesc();
        } catch (Exception e) {
            log.error("Error finding latest space marine: {}", e.getMessage());
            return null;
        }
    }

    public Map<String, Long> getChapterStatistics() {
        Map<String, Long> stats = new HashMap<>();
        try {
            List<Object[]> results = spaceMarineRepository.countByChapterGroup();
            for (Object[] result : results) {
                Chapter chapter = (Chapter) result[0];
                Long count = (Long) result[1];
                stats.put(chapter.getName(), count);
            }
        } catch (Exception e) {
            log.error("Error getting chapter statistics: {}", e.getMessage());
        }
        return stats;
    }

    public Long countByHealthGreaterThan(Float health) {
        try {
            return spaceMarineRepository.countByHealthGreaterThan(health);
        } catch (Exception e) {
            log.error("Error counting by health: {}", e.getMessage());
            return 0L;
        }
    }
}
