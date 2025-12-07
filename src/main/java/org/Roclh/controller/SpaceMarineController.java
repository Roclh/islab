package org.Roclh.controller;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.model.Chapter;
import org.Roclh.model.Coordinates;
import org.Roclh.model.MeleeWeapon;
import org.Roclh.model.SpaceMarine;
import org.Roclh.service.ChapterService;
import org.Roclh.service.SpaceMarineService;
import org.Roclh.service.WebSocketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/spacemarines")
public class SpaceMarineController {

    private final SpaceMarineService spaceMarineService;
    private final ChapterService chapterService;
    private final WebSocketService webSocketService;

    @GetMapping
    public String listSpaceMarines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String filter,
            Model model) {

        log.info("Loading spacemarines list - page: {}, size: {}, sortField: {}, sortDirection: {}, filter: {}",
                page, size, sortField, sortDirection, filter);

        try {
            // Создаем объект сортировки
            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortField).descending()
                    : Sort.by(sortField).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<SpaceMarine> spaceMarinePage;

            if (filter != null && !filter.trim().isEmpty()) {
                spaceMarinePage = spaceMarineService.findByNameContaining(filter.trim(), pageable);
            } else {
                spaceMarinePage = spaceMarineService.findAll(pageable);
            }

            model.addAttribute("spaceMarines", spaceMarinePage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", spaceMarinePage.getTotalPages());
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("filter", filter);
            model.addAttribute("chapters", chapterService.findAll());

            log.info("Successfully loaded {} space marines", spaceMarinePage.getNumberOfElements());
            return "spacemarines/list";

        } catch (Exception e) {
            log.error("Error loading space marines list: {}", e.getMessage(), e);
            model.addAttribute("error", "Error loading space marines: " + e.getMessage());
            return "spacemarines/list";
        }
    }

    @GetMapping("/{id}")
    public String viewSpaceMarine(@PathVariable Long id, Model model) {
        Optional<SpaceMarine> spaceMarine = spaceMarineService.findById(id);
        if (spaceMarine.isPresent()) {
            SpaceMarine marine = spaceMarine.get();
            // Гарантируем, что heartCount не null
            if (marine.getHeartCount() < 1 || marine.getHeartCount() > 3) {
                marine.setHeartCount(2); // Устанавливаем значение по умолчанию
            }
            model.addAttribute("spaceMarine", marine);
            return "spacemarines/view";
        }
        return "redirect:/spacemarines";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("weapons", MeleeWeapon.values());
        model.addAttribute("chapters", chapterService.findAll());
        return "spacemarines/create";
    }

    @PostMapping("/create")
    public String createSpaceMarine(
            @RequestParam String name,
            @RequestParam Integer x,
            @RequestParam(defaultValue = "0") long y,
            @RequestParam Float health,
            @RequestParam int heartCount,
            @RequestParam(required = false) Long height,
            @RequestParam MeleeWeapon meleeWeapon,
            @RequestParam Long chapterId,
            Model model) {

        try {
            Optional<Chapter> chapter = chapterService.findById(chapterId);
            if (chapter.isEmpty()) {
                model.addAttribute("error", "Chapter not found");
                model.addAttribute("weapons", MeleeWeapon.values());
                model.addAttribute("chapters", chapterService.findAll());
                return "spacemarines/create";
            }
            Chapter chapter1 = chapter.get();
            if(chapter1.getMarinesCount() > 1000) {
                throw new ValidationException("Space marines count breached 1000!");
            }

            // Создаем координаты
            Coordinates coordinates = new Coordinates();
            coordinates.setX(x);
            coordinates.setY(y);

            // Создаем SpaceMarine
            SpaceMarine spaceMarine = new SpaceMarine();
            spaceMarine.setName(name);
            spaceMarine.setCoordinates(coordinates);
            spaceMarine.setHealth(health);
            spaceMarine.setHeartCount(heartCount);
            spaceMarine.setHeight(height);
            spaceMarine.setMeleeWeapon(meleeWeapon);
            spaceMarine.setChapter(chapter1);
            chapter1.setMarinesCount(chapter1.getMarinesCount() + 1);
            chapterService.save(chapter1);
            spaceMarineService.save(spaceMarine);
            webSocketService.notifySpaceMarineUpdate();
            return "redirect:/spacemarines";

        } catch (Exception e) {
            model.addAttribute("error", "Error creating space marine: " + e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("x", x);
            model.addAttribute("y", y);
            model.addAttribute("health", health);
            model.addAttribute("heartCount", heartCount);
            model.addAttribute("height", height);
            model.addAttribute("meleeWeapon", meleeWeapon);
            model.addAttribute("chapterId", chapterId);
            model.addAttribute("weapons", MeleeWeapon.values());
            model.addAttribute("chapters", chapterService.findAll());
            return "spacemarines/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<SpaceMarine> spaceMarine = spaceMarineService.findById(id);
        if (spaceMarine.isPresent()) {
            SpaceMarine marine = spaceMarine.get();
            model.addAttribute("id", marine.getId());
            model.addAttribute("name", marine.getName());
            model.addAttribute("x", marine.getCoordinates().getX());
            model.addAttribute("y", marine.getCoordinates().getY());
            model.addAttribute("health", marine.getHealth());
            model.addAttribute("heartCount", marine.getHeartCount());
            model.addAttribute("height", marine.getHeight());
            model.addAttribute("meleeWeapon", marine.getMeleeWeapon());
            model.addAttribute("chapterId", marine.getChapter().getId());
            model.addAttribute("weapons", MeleeWeapon.values());
            model.addAttribute("chapters", chapterService.findAll());
            return "spacemarines/edit";
        }
        return "redirect:/spacemarines";
    }

    @PostMapping("/{id}/edit")
    public String updateSpaceMarine(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam Integer x,
            @RequestParam(defaultValue = "0") long y,
            @RequestParam Float health,
            @RequestParam int heartCount,
            @RequestParam(required = false) Long height,
            @RequestParam MeleeWeapon meleeWeapon,
            @RequestParam Long chapterId,
            Model model) {

        try {
            Optional<SpaceMarine> existing = spaceMarineService.findById(id);
            if (existing.isEmpty()) {
                return "redirect:/spacemarines";
            }

            Optional<Chapter> chapter = chapterService.findById(chapterId);
            if (chapter.isEmpty()) {
                model.addAttribute("error", "Chapter not found");
                model.addAttribute("weapons", MeleeWeapon.values());
                model.addAttribute("chapters", chapterService.findAll());
                return "spacemarines/edit";
            }

            SpaceMarine spaceMarine = existing.get();
            spaceMarine.setName(name);

            // Обновляем координаты
            Coordinates coordinates = spaceMarine.getCoordinates();
            coordinates.setX(x);
            coordinates.setY(y);

            spaceMarine.setHealth(health);
            spaceMarine.setHeartCount(heartCount);
            spaceMarine.setHeight(height);
            spaceMarine.setMeleeWeapon(meleeWeapon);
            Chapter newChapter = chapter.get();
            if (!spaceMarine.getChapter().getId().equals(id)) {
                Chapter previous = spaceMarine.getChapter();
                previous.setMarinesCount(previous.getMarinesCount() - 1);
                newChapter.setMarinesCount(newChapter.getMarinesCount() + 1);
                chapterService.save(previous);
                chapterService.save(newChapter);
            }
            spaceMarine.setChapter(newChapter);
            spaceMarineService.save(spaceMarine);
            webSocketService.notifySpaceMarineUpdate();
            return "redirect:/spacemarines";

        } catch (Exception e) {
            model.addAttribute("error", "Error updating space marine: " + e.getMessage());
            model.addAttribute("id", id);
            model.addAttribute("name", name);
            model.addAttribute("x", x);
            model.addAttribute("y", y);
            model.addAttribute("health", health);
            model.addAttribute("heartCount", heartCount);
            model.addAttribute("height", height);
            model.addAttribute("meleeWeapon", meleeWeapon);
            model.addAttribute("chapterId", chapterId);
            model.addAttribute("weapons", MeleeWeapon.values());
            model.addAttribute("chapters", chapterService.findAll());
            return "spacemarines/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<String> deleteSpaceMarine(@PathVariable Long id) {
        spaceMarineService.deleteWithChapterUpdate(id);
        webSocketService.notifySpaceMarineUpdate();
        return ResponseEntity.ok().build();
    }
}