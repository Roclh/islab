
package org.Roclh.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.model.Chapter;
import org.Roclh.repository.ChapterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final WebSocketService webSocketService;

    public List<Chapter> findAll() {
        return chapterRepository.findAll();
    }

    public Optional<Chapter> findById(Long id) {
        return chapterRepository.findById(id);
    }

    public Chapter save(Chapter chapter) {
        Chapter saved = chapterRepository.save(chapter);
        log.info("Chapter saved, sending WebSocket notification");
        webSocketService.notifySpaceMarineUpdate();
        return saved;
    }

    public void deleteById(Long id) {
        chapterRepository.deleteById(id);
        log.info("Chapter deleted, sending WebSocket notification");
        webSocketService.notifySpaceMarineUpdate();
    }
}
