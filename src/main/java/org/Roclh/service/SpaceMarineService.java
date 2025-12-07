package org.Roclh.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.model.Chapter;
import org.Roclh.model.SpaceMarine;
import org.Roclh.repository.ChapterRepository;
import org.Roclh.repository.SpaceMarineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpaceMarineService {

    private final SpaceMarineRepository spaceMarineRepository;
    private final ChapterRepository chapterRepository;
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
        return saved;
    }

    public void deleteWithChapterUpdate(Long id) {
        try {
            SpaceMarine spaceMarine = spaceMarineRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("SpaceMarine not found"));
            Chapter chapter = spaceMarine.getChapter();
            if (chapter != null) {
                chapter.setMarinesCount(chapter.getMarinesCount() - 1);
                chapterRepository.save(chapter);
            }
            int amount = spaceMarineRepository.deleteMarineById(id);
            log.info("{} Space Marines deleted with chapter update, sending WebSocket notification", amount);
        } catch (Exception e) {
            log.error("Ошибка удаления марина: ", e);
        }
    }

    public SpaceMarine findLatest() {
        try {
            return spaceMarineRepository.findTopByOrderByCreationDateDesc();
        } catch (Exception e) {
            log.error("Error finding latest space marine: {}", e.getMessage());
            return null;
        }
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
